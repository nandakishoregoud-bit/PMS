package com.pcs.serviceImpl;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.pcs.model.LoginRecord;
import com.pcs.repository.LoginRecordRepository;
import com.pcs.service.LoginRecordService;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;


@Service
public class LoginRecordServiceImpl implements LoginRecordService {
	
	private static final String[] HEADERS = { "Employee ID", "Employee Name", "Login Time", "Logout Time", "Duration" };



    @Autowired
    private LoginRecordRepository loginRecordRepository;

    @Override
    public LoginRecord recordLogin(String employeeId, String employeeName) {
        // Check if there's an active session
        Optional<LoginRecord> latest = loginRecordRepository
                .findTopByEmployeeIdAndLogoutTimeIsNullOrderByLoginTimeDesc(employeeId);

        // If there's an active session, close it
        if (latest.isPresent()) {
            LoginRecord record = latest.get();
            LocalDateTime now = LocalDateTime.now();
            record.setLogoutTime(now);

            Duration duration = Duration.between(record.getLoginTime(), now);
            long totalSeconds = duration.getSeconds();
            long hours = totalSeconds / 3600;
            long minutes = (totalSeconds % 3600) / 60;
            long seconds = totalSeconds % 60;

            String formattedDuration = hours + "h " + minutes + "m " + seconds + "s";
            record.setDuration(formattedDuration);

            loginRecordRepository.save(record); // Save the updated session
        }

        // Create new login session
        LoginRecord loginRecord = new LoginRecord(employeeId, employeeName, LocalDateTime.now());
        return loginRecordRepository.save(loginRecord); // Save new session
    }

    @Override
    public List<LoginRecord> getLoginRecordsByEmployee(String employeeId) {
        return loginRecordRepository.findByEmployeeId(employeeId);
    }
    
    @Override
    public LoginRecord recordLogout(String employeeId) {
        Optional<LoginRecord> latest = loginRecordRepository
            .findTopByEmployeeIdAndLogoutTimeIsNullOrderByLoginTimeDesc(employeeId);

        if (latest.isPresent()) {
            LoginRecord record = latest.get();
            LocalDateTime now = LocalDateTime.now();
            record.setLogoutTime(now);

            Duration duration = Duration.between(record.getLoginTime(), now);
            long totalSeconds = duration.getSeconds();
            long hours = totalSeconds / 3600;
            long minutes = (totalSeconds % 3600) / 60;
            long seconds = totalSeconds % 60;

            String formattedDuration = hours + "h " + minutes + "m " + seconds + "s";
            record.setDuration(formattedDuration);

            return loginRecordRepository.save(record);
        } else {
            throw new RuntimeException("No active login session found for this employee.");
        }
    }

    @Override
    public List<LoginRecord> getAllLoginRecords() {
        return loginRecordRepository.findAll();
    }
   @Override
    public Page<LoginRecord> searchLoginRecords(String employeeId, LocalDateTime from, LocalDateTime to, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "loginTime"));

        if (employeeId != null && from != null && to != null) {
            return loginRecordRepository.findByEmployeeIdAndLoginTimeBetween(employeeId, from, to, pageable);
        } else if (employeeId != null) {
            return loginRecordRepository.findByEmployeeId(employeeId, pageable);
        } else if (from != null && to != null) {
            return loginRecordRepository.findByLoginTimeBetween(from, to, pageable);
        } else {
            return loginRecordRepository.findAll(pageable);
        }
    }

   
   @Override
   public ByteArrayInputStream exportToExcel(List<LoginRecord> records) throws IOException {
       try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
           Sheet sheet = workbook.createSheet("Login Records");

           // Header row
           Row headerRow = sheet.createRow(0);
           for (int i = 0; i < HEADERS.length; i++) {
               headerRow.createCell(i).setCellValue(HEADERS[i]);
           }

           // Data rows
           int rowIdx = 1;
           for (LoginRecord record : records) {
               Row row = sheet.createRow(rowIdx++);
               row.createCell(0).setCellValue(record.getEmployeeId());
               row.createCell(1).setCellValue(record.getEmployeeName());
               row.createCell(2).setCellValue(safeToString(record.getLoginTime()));
               row.createCell(3).setCellValue(safeToString(record.getLogoutTime()));
               row.createCell(4).setCellValue(record.getDuration() != null ? record.getDuration() : "N/A");
           }

           workbook.write(out);
           return new ByteArrayInputStream(out.toByteArray());
       }
   }

   @Override
   public ByteArrayInputStream exportToPDF(List<LoginRecord> records) throws IOException {
       try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
           Document document = new Document();
           PdfWriter.getInstance(document, out);
           document.open();

           Font titleFont = new Font(Font.HELVETICA, 16, Font.BOLD);
           Paragraph title = new Paragraph("Login Records", titleFont);
           title.setAlignment(Element.ALIGN_CENTER);
           document.add(title);
           document.add(new Paragraph("\n"));

           PdfPTable table = new PdfPTable(5);
           table.setWidthPercentage(100);

           for (String header : HEADERS) {
               table.addCell(header);
           }

           for (LoginRecord record : records) {
               table.addCell(record.getEmployeeId());
               table.addCell(record.getEmployeeName());
               table.addCell(safeToString(record.getLoginTime()));
               table.addCell(safeToString(record.getLogoutTime()));
               table.addCell(record.getDuration() != null ? record.getDuration() : "N/A");
           }

           document.add(table);
           document.close();

           return new ByteArrayInputStream(out.toByteArray());
       } catch (DocumentException e) {
           throw new RuntimeException("Error creating PDF", e);
       }
   }

   private String safeToString(Object value) {
       return value != null ? value.toString() : "N/A";
   }
    
}