package org.fhmdb.auth.service;

import org.fhmdb.auth.model.User;
import org.fhmdb.auth.repository.UserRepository;
import org.fhmdb.auth.security.JwtUtil;
import org.fhmdb.auth.util.MultipartInputStreamFileResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class FaceAuthService {

    private final RestTemplate restTemplate;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final String fastApiUrl;
    private static final Logger logger = LoggerFactory.getLogger(FaceAuthService.class);


    public FaceAuthService(
            RestTemplateBuilder builder,
            UserRepository userRepository,
            JwtUtil jwtUtil,
            @Value("${faceapi.recognize-url}") String fastApiUrl
    ) {
        this.restTemplate = builder.build();
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.fastApiUrl = fastApiUrl;
    }



    public Long recognizeFaceAndGetId(List<MultipartFile> photos) throws IOException {
        if (photos == null || photos.isEmpty()) {
            logger.warn("No photos provided for face recognition");
            throw new IllegalArgumentException("At least one photo is required");
        }

        if (photos.size() > 5) {
            logger.warn("Too many photos provided: {}", photos.size());
            throw new IllegalArgumentException("Maximum 5 photos are allowed");
        }

        logger.info("Sending {} photo(s) to face recognition service", photos.size());

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        for (MultipartFile photo : photos) {
            MultipartInputStreamFileResource fileResource =
                    new MultipartInputStreamFileResource(photo.getInputStream(), photo.getOriginalFilename());
            body.add("photos", fileResource);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(fastApiUrl, requestEntity, Map.class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            Object id = response.getBody().get("id");
            if (id instanceof Number) {
                Long faceId = ((Number) id).longValue();
                logger.info("Face recognized, ID = {}", faceId);
                return faceId;
            } else {
                logger.error("Invalid ID format from face service: {}", id);
                throw new IllegalStateException("Returned ID is not numeric: " + id);
            }
        }

        logger.error("Face recognition service failed: {}", response.getStatusCode());
        throw new RuntimeException("Face recognition failed: " + response.getStatusCode());
    }


    public Optional<User> getUserByFaceId(Long faceId) {
        logger.debug("Looking for user with faceId {}", faceId);
        return userRepository.findByFaceId(faceId);
    }

    public String generateToken(User user) {
        logger.info("Generating JWT for user {}", user.getEmail());
        return jwtUtil.generateToken(user.getEmail());
    }

}
