package me.gensh.network;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.security.Security;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class MailSender extends javax.mail.Authenticator { 
	private String user;
	private String password;
	private Session session;
	private String mailhost = "smtp.qq.com";//
	private Multipart messageMultipart;
	private Properties properties;
	static {
		Security.addProvider(new JSSEProvider());
	}

	public MailSender(String user, String password) {
		this.user = user;
		this.password = password;

		properties = new Properties();
		properties.setProperty("mail.transport.protocol", "smtp");
		properties.setProperty("mail.host", mailhost);
		properties.put("mail.smtp.auth", "true");
		properties.put("mail.smtp.port", "465");
		properties.put("mail.smtp.socketFactory.port", "465");
		properties.put("mail.smtp.socketFactory.class",
				"javax.net.ssl.SSLSocketFactory");
		properties.put("mail.smtp.socketFactory.fallback", "false");
		properties.setProperty("mail.smtp.quitwait", "false");

		session = Session.getDefaultInstance(properties, this);
		messageMultipart=new MimeMultipart();
	}

	protected PasswordAuthentication getPasswordAuthentication() {
		return new PasswordAuthentication(user, password);
	}

	public synchronized void sendMail(String subject, String body,
			String sender, String recipients,String attachment) throws Exception {
		MimeMessage message = new MimeMessage(session);
		message.setSender(new InternetAddress(sender));
		message.setSubject(subject);
		BodyPart bodyPart=new MimeBodyPart();
		bodyPart.setText(body);
		messageMultipart.addBodyPart(bodyPart);
//		message.setDataHandler(handler);
		
		//
		if(attachment!=null){
			DataSource dataSource=new FileDataSource(attachment);
			DataHandler dataHandler=new DataHandler(dataSource);
			bodyPart.setDataHandler(dataHandler);
			bodyPart.setFileName(attachment.substring(attachment.lastIndexOf("/")+1));
		}
		message.setContent(messageMultipart);
		if (recipients.indexOf(',') > 0)
			//
			message.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse(recipients));
		else
			//
			message.setRecipient(Message.RecipientType.TO, new InternetAddress(
					recipients));
		Transport.send(message);
	}

	//
	public class ByteArrayDataSource implements DataSource {
		private byte[] data;
		private String type;

		public ByteArrayDataSource(byte[] data, String type) {
			super();
			this.data = data;
			this.type = type;
		}

		public ByteArrayDataSource(byte[] data) {
			super();
			this.data = data;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getContentType() {
			if (type == null)
				return "application/octet-stream";
			else
				return type;
		}

		public InputStream getInputStream() throws IOException {
			return new ByteArrayInputStream(data);
		}

		public String getName() {
			return "ByteArrayDataSource";
		}

		public OutputStream getOutputStream() throws IOException {
			throw new IOException("Not Supported");
		}

		public PrintWriter getLogWriter() throws SQLException {
			// TODO Auto-generated method stub
			return null;
		}

		public int getLoginTimeout() throws SQLException {
			// TODO Auto-generated method stub
			return 0;
		}

		public void setLogWriter(PrintWriter out) throws SQLException {
			// TODO Auto-generated method stub

		}

		public void setLoginTimeout(int seconds) throws SQLException {
			// TODO Auto-generated method stub

		}

		public boolean isWrapperFor(Class<?> arg0) throws SQLException {
			// TODO Auto-generated method stub
			return false;
		}

		public <T> T unwrap(Class<T> arg0) throws SQLException {
			// TODO Auto-generated method stub
			return null;
		}

		public Connection getConnection() throws SQLException {
			// TODO Auto-generated method stub
			return null;
		}

		public Connection getConnection(String theUsername, String thePassword)
				throws SQLException {
			// TODO Auto-generated method stub
			return null;
		}
	}

	public String getMailhost() {
		return mailhost;
	}

	public void setMailhost(String mailhost) {
		this.mailhost = mailhost;
		properties.setProperty("mail.host", this.mailhost);
	}
		 
}
