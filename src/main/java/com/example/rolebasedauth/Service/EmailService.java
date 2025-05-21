package com.example.rolebasedauth.Service;

import com.example.rolebasedauth.Dto.EmailDto;
import com.example.rolebasedauth.Entity.Email;
import com.example.rolebasedauth.Repository.EmailRepository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final EmailRepository emailRepository;
    
    @Autowired
    public EmailService(EmailRepository emailRepository) {
        this.emailRepository = emailRepository;
    }
    
    public Email saveEmail(EmailDto emailDTO) {
        Email email = new Email(
            emailDTO.getName(),
            emailDTO.getEmail(),
            emailDTO.getSubject(),
            emailDTO.getMessage()
        );
        
        return emailRepository.save(email);
    }

public List<Email> getAllEmails() {
    return emailRepository.findAll();
}

public Page<Email> getEmailsWithPagination(int page, int size) {
    return emailRepository.findAll(PageRequest.of(page, size));
}
}
