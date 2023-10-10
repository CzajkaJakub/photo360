package pl.put.photo360.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;
import pl.put.photo360.config.Configuration;
import pl.put.photo360.entity.UserDataEntity;
import pl.put.photo360.shared.dto.ServerResponseCode;
import pl.put.photo360.shared.exception.ServiceException;

@Service
public class EmailService
{

    private final JavaMailSender emailSender;
    private final Configuration configuration;
    private final Logger logger = LoggerFactory.getLogger( EmailService.class );

    @Autowired
    public EmailService( JavaMailSender emailSender, Configuration configuration )
    {
        this.emailSender = emailSender;
        this.configuration = configuration;
    }

    public void sendEmailVerification( UserDataEntity userEntity )
    {
        var emailContent = String.format( configuration.getEMAIL_VERIFICATION_TEXT(), userEntity.getLogin(),
            userEntity.getEmailVerificationToken() );
        var emailSubject = configuration.getEMAIL_VERIFICATION_SUBJECT();
        sendEmail( userEntity.getEmail(), emailSubject, emailContent );
    }

    public void sendResetPasswordEmail( UserDataEntity userEntity )
    {
        var emailContent = String.format( configuration.getRESET_PASSWORD_EMAIL_TEXT(), userEntity.getLogin(),
            userEntity.getResetPasswordToken() );
        var emailSubject = configuration.getRESET_PASSWORD_EMAIL_SUBJECT();
        sendEmail( userEntity.getEmail(), emailSubject, emailContent );
    }

    public void sendEmail( String recipient, String emailSubject, String emailContent )
    {
        try
        {
            MimeMessage mimeMessage = emailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper( mimeMessage, "UTF-8" );
            messageHelper.setText( emailContent, true );
            messageHelper.setTo( recipient );
            messageHelper.setSubject( emailSubject );
            emailSender.send( mimeMessage );
        }
        catch( Exception aEx )
        {
            logger.error( aEx.getMessage() );
            logger.error( ServerResponseCode.STATUS_EMAIL_SEND_FAILED.getResponseMessage() );
            // throw new ServiceException( ServerResponseCode.STATUS_EMAIL_SEND_FAILED );
        }
    }
}