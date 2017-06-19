package farto.cleva.guilherme.main;

import java.io.File;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;
import farto.cleva.guilherme.constants.StandardCharsets;
import farto.cleva.guilherme.constants.StandardMimeTypes;
import freemarker.template.Configuration;
import freemarker.template.Template;

public class Main {

	// ### GMail Authentication (Less Secure Apps)
	// ### https://myaccount.google.com/lesssecureapps

	private static final String USERNAME = "USERNAME-FOR-GMAIL@gmail.com";
	private static final String PASSWORD = "PASSWORD-FOR-USERNAME-GMAIL";

	// ### Yahoo Authentication (Security Other Apps)
	// ### https://login.yahoo.com/account/security#other-apps

	// private static final String USERNAME = "USERNAME-FOR-YAHOO@yahoo.com.br";
	// private static final String PASSWORD = "PASSWORD-FOR-USERNAME-YAHOO";

	private static final String FROM = USERNAME;
	private static final String TO = "MAIL1@gmail.com , MAIL2@yahoo.com.br";
	private static final String CC = "MAIL3@gmail.com";

	private static final String SUBJECT = "Sample mail with JavaMail API";

	private static String BODY_CONTENT;

	private static final String RESOURCES_DIR = "src/main/resources/";

	private static final String FILES_RESOURCES_DIR = RESOURCES_DIR + "files/";

	private static final String TEMPLATES_RESOURCES_DIR = "/templates/";

	private static final List<String> ATTACHMENTS = new LinkedList<String>(Arrays.asList("Pluviometric Data - Farms.pdf", "Pluviometric Data - Weather Stations.pdf"));

	private static final String MAIL_BODY_TEMPLATE = "mail_body.ftl";

	static {

		// ##### FREEMARKER TEMPLATE BODY #####

		Configuration fmConfiguration = new Configuration(Configuration.VERSION_2_3_23);
		fmConfiguration.setClassForTemplateLoading(Main.class, TEMPLATES_RESOURCES_DIR);
		fmConfiguration.setDefaultEncoding(StandardCharsets.UTF_8);
		fmConfiguration.setLocale(Locale.US);

		try {

			Map<String, Object> data = new HashMap<String, Object>();
			data.put("banner", "https://interno.totvs.com/mktfiles/gooddata/agro/banner_totvs.png");
			data.put("name", "Guilherme");
			data.put("author", "Guilherme de Cleva Farto");
			data.put("attachments", ATTACHMENTS);

			Template template = fmConfiguration.getTemplate(MAIL_BODY_TEMPLATE);

			StringWriter sw = new StringWriter();

			template.process(data, sw);

			BODY_CONTENT = sw.toString();

			sw.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {

		try {

			Properties props = new Properties();
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.starttls.enable", "true");

			props.put("mail.transport.protocol", "smtp");
			props.put("mail.store.protocol", "pop3");

			props.put("mail.smtp.host", "smtp.gmail.com");
			// props.put("mail.smtp.host", "smtp.mail.yahoo.com");

			// props.put("mail.pop3.host", "value-for-pop3-host"); // If the POP3 protocol is used

			props.put("mail.smtp.port", "587");
			props.put("mail.smtp.socketFactory.port", "587");
			props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

			props.put("mail.debug", "false"); // Enable debugging output from the JavaMail classes

			Session session = Session.getInstance(props, new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(USERNAME, PASSWORD);
				}
			});

			MimeMessage message = new MimeMessage(session);
			Multipart multipartMessage = new MimeMultipart();
			MimeBodyPart messageBodyPart = new MimeBodyPart();
			MimeBodyPart messageAttachmentPart = null;

			message.setFrom(new InternetAddress(FROM));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(TO));
			message.setRecipients(Message.RecipientType.CC, InternetAddress.parse(CC));

			messageBodyPart.setContent(BODY_CONTENT, StandardMimeTypes.TEXT_HTML + "; charset=" + StandardCharsets.UTF_8);
			multipartMessage.addBodyPart(messageBodyPart);

			for (String attachment : ATTACHMENTS) {
				File fileAttachment = new File(FILES_RESOURCES_DIR + attachment);

				messageAttachmentPart = new MimeBodyPart();
				messageAttachmentPart.setDataHandler(new DataHandler(new FileDataSource(fileAttachment)));
				messageAttachmentPart.setFileName(MimeUtility.encodeText(fileAttachment.getName(), StandardCharsets.UTF_8, null));
				multipartMessage.addBodyPart(messageAttachmentPart);
			}

			message.setSubject(SUBJECT, StandardCharsets.UTF_8);
			message.setContent(multipartMessage);
			message.setSentDate(new Date());

			Transport.send(message);

			System.out.println("Email successfully sent");

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
