package com.jenish.demo.service;

import java.io.File;
import java.sql.Date;
import java.util.List;

import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.IBlockElement;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.jenish.demo.model.Attendance;
import com.jenish.demo.model.Student;
import com.jenish.demo.repository.AttendanceRepository;
import com.jenish.demo.repository.StudentRepository;
import com.jenish.demo.repository.TimeLogRepository;

@Service
public class EmailAndPDF {

	@Autowired
	JavaMailSender javaMailSender;

	@Autowired
	AttendanceRepository atnRepo;

	@Autowired
	StudentRepository stuRepo;

	@Autowired
	TimeLogRepository timeRepo;

	@Value("${spring.mail.username}")
	private String sender;

	public String generateAndSend() {
		try {//------
			Thread.sleep(2000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}//----
		String result = null;
		List<Student> students = stuRepo.findAll();

		for (Student stu : students) {
			try {
				result = sendMailWithAttachment(stu);
				System.out.println(stu.getName() + " Finished..");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				result = "Some problem";
				break;
			}
		}

		return result;
	}

	private String sendMailWithAttachment(Student stu) throws Exception {
		try {

			MimeMessage mimeMessage = javaMailSender.createMimeMessage();
			MimeMessageHelper mimeMsgHelper = new MimeMessageHelper(mimeMessage, true);

			mimeMsgHelper.setFrom(sender);
			mimeMsgHelper.setTo(stu.getEmail());
			mimeMsgHelper.setSubject("Checkin Details");
			mimeMsgHelper.setText("Hi " + stu.getName() + ", find below the attachment...");

			FileSystemResource file = new FileSystemResource(new File(createPdf(stu)));

			mimeMsgHelper.addAttachment(file.getFilename(), file);
			//javaMailSender.send(mimeMessage);
			return "Sent mail with attachment...";
		} catch (Exception e) {
			throw e;
		}
	}

	private String createPdf(Student stu) {

		String filePdf = "C:\\Jenish\\ProjectsPractice\\Attendance\\src\\files\\StuCheck" + stu.getId() + ".pdf";

		List<Attendance> atns = atnRepo.findByStuId(stu.getId());

		try {
			PdfWriter writer = new PdfWriter(filePdf);
			PdfDocument pdfDoc = new PdfDocument(writer);
			Document document = new Document(pdfDoc);
			Paragraph p1 = new Paragraph("Hello " + stu.getName() + ", Greetings from PRODIAN INFOTECH PVT LTD.\n\tCheck here your login details.");
			float[] columnWidth = { 300f, 300f };
			Table table = new Table(columnWidth);
			table.addCell(new Cell().add("Name: " + stu.getName()));
			table.addCell(new Cell().add("Student Id: " + stu.getId() + "\nEmail: " + stu.getEmail()));
			document.add(p1);
			document.add(table);
			float[] columnWidth1 = { 120f, 120f, 120f, 120f, 120f };
			table = new Table(columnWidth1);
			table.addCell(new Cell().add("Date"));
			table.addCell(new Cell().add("LoginTime"));
			table.addCell(new Cell().add("LogoutTime"));
			table.addCell(new Cell().add("WorkedTime"));
			table.addCell(new Cell().add("Status"));
			for (Attendance at : atns) {

				table.addCell(new Cell().add(getAsString(at.getDate())));
				table.addCell(new Cell().add(at.getLogIn().toString()));
				if(at.getLogOut()!=null)
					table.addCell(new Cell().add(at.getLogOut().toString()));
				else
					table.addCell(new Cell().add("00:00:00"));
				table.addCell(new Cell().add(at.getWorkedTime().toString()));
				table.addCell(new Cell().add(at.getStatus()));

			}

			//document.add(p1);
			document.add(table);
			document.close();
			return filePdf;
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return filePdf;
		}
	}

	private String getAsString(Date date) {
		String strDate = date.toString();
		String[] sepDate = strDate.split("-");
		strDate = "";
		for (int i = 0; i < sepDate.length; i++) {
			if (i != 0)
				strDate += "/";
			strDate += sepDate[sepDate.length - i - 1];
		}
		return strDate;
	}

}
