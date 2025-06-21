package com.pcs.serviceImpl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.pcs.model.DailyReport;
import com.pcs.model.Employee;
import com.pcs.repository.DailyReportRepository;
import com.pcs.repository.EmployeeRepository;
import com.pcs.service.DailyReportService;


@Service
public class DailyReportServiceImpl implements DailyReportService {

    @Autowired
    private DailyReportRepository dailyReportRepository;
    
    @Autowired
    private EmployeeRepository employeeRepository;
    
    @Autowired
    private EmailService emailService;

    @Override
    public DailyReport submitReport(DailyReport report) {
        String empId = report.getEmployeeId();
        String empId2 = report.getSubmitTo();

        Optional<Employee> employeeOpt = employeeRepository.findByEmpId(empId);

        if (employeeOpt.isEmpty()) {
            throw new IllegalArgumentException("Employee not found with ID: " + empId);
        }

        Employee submitter = employeeOpt.get();

        if (!"Approved".equals(submitter.getStatus())) {
            throw new IllegalStateException("Employee is not approved");
        }

        LocalDateTime now = LocalDateTime.now();
        report.setSubmittedDate(now);
        report.setFormattedDate(now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        DailyReport savedReport = dailyReportRepository.save(report);

        // If submitTo is provided, send email
        if (empId2 != null && !empId2.isEmpty()) {
            Optional<Employee> receiverOpt = employeeRepository.findByEmpId(empId2);
            if (receiverOpt.isPresent()) {
                String fromName = submitter.getName() + " <" + submitter.getEmail() + ">";
                String toEmail = receiverOpt.get().getEmail();
                String subject = "New Report Submitted by " + submitter.getName();
                String content = "<p>Hello " + receiverOpt.get().getName() + ",</p>"
                               + "<p>You have received a new report from " + submitter.getName() + ".</p>"
                               + "<p><strong>Title:</strong> " + report.getTitle() + "</p>"
                               + "<p><strong>Hours Spent:</strong> " + report.getHoursSpend() + "</p>"
                               + "<p><strong>Content:</strong><br/>" + report.getReportContent() + "</p>"
                               + "<br/><p>Regards,<br/>Report System</p>";

                try {
                    emailService.sendReportEmail(fromName, toEmail, subject, content);
                } catch (MessagingException e) {
                    throw new RuntimeException("Failed to send email: " + e.getMessage());
                } catch (UnsupportedEncodingException e) {
					
					e.printStackTrace();
				}
            }
        }

        return savedReport;
    }


    @Override
    public List<DailyReport> getAllReports() {
        return dailyReportRepository.findAll();
    }

    @Override
    public List<DailyReport> getReportsByEmployee(String employeeId) {
        return dailyReportRepository.findByEmployeeId(employeeId);
    }
    
    @Override
    public List<DailyReport> getReportsByEmployeeAndMonthYear(String employeeId, int month, int year) {
        LocalDateTime start = LocalDateTime.of(year, month, 1, 0, 0);
        LocalDateTime end = start.plusMonths(1);
        return dailyReportRepository.findByEmployeeIdAndSubmittedDateBetween(employeeId, start, end);
    }

    @Override
    public List<DailyReport> getReportsByEmployeeAndYear(String employeeId, int year) {
        LocalDateTime start = LocalDateTime.of(year, 1, 1, 0, 0);
        LocalDateTime end = start.plusYears(1);
        return dailyReportRepository.findByEmployeeIdAndSubmittedDateBetween(employeeId, start, end);
    }
    
    @Override
    public void exportToExcel(List<DailyReport> reports, HttpServletResponse response, String fileName, String employeeId) throws IOException {
        String fullFileName = fileName + "_" + employeeId + ".xlsx";
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=" + fullFileName);

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Daily Reports");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Employee Id");
        header.createCell(3).setCellValue("Date");
        header.createCell(4).setCellValue("Hours Spend");
        header.createCell(1).setCellValue("Title");
        header.createCell(2).setCellValue("Content");

        int rowIdx = 1;
        for (DailyReport report : reports) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(report.getEmployeeId());
            row.createCell(3).setCellValue(report.getFormattedDate());
            row.createCell(4).setCellValue(report.getHoursSpend());
            row.createCell(1).setCellValue(report.getTitle());
            row.createCell(2).setCellValue(report.getReportContent());
        }

        workbook.write(response.getOutputStream());
        workbook.close();
    }

    @Override
    public void exportToPDF(List<DailyReport> reports, HttpServletResponse response, String fileName, String employeeId) throws IOException {
        String fullFileName = fileName + "_" + employeeId + ".pdf";
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=" + fullFileName);

        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();

        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        Paragraph title = new Paragraph("Employee Daily Reports", titleFont);
        title.setAlignment(Paragraph.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);

        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        table.setWidths(new int[]{5, 4, 5, 4,5});

        addTableHeader(table,"EmployeeId","Date","Hours Spend", "Title", "Content");

        for (DailyReport report : reports) {
            table.addCell(report.getEmployeeId());
            table.addCell(report.getFormattedDate());
            table.addCell(report.getHoursSpend());
            table.addCell(report.getTitle());
            table.addCell(report.getReportContent());
        }

        document.add(table);
        document.close();
    }


    private void addTableHeader(PdfPTable table, String... headers) {
        for (String header : headers) {
            PdfPCell cell = new PdfPCell();
            cell.setPhrase(new Phrase(header, FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
            table.addCell(cell);
        }
    }
    @Override
    public List<DailyReport> getReportsBetweenDates(LocalDate from, LocalDate to) {
        return dailyReportRepository.findBySubmittedDateBetween(from.atStartOfDay(), to.atTime(LocalTime.MAX));
    }

	

}
