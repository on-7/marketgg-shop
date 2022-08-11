package com.nhnacademy.marketgg.server.controller.file;

import com.nhnacademy.marketgg.server.dto.response.file.ImageResponse;
import com.nhnacademy.marketgg.server.service.file.FileService;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/storage")
@Slf4j
public class FileController {

    private final FileService fileService;

    @GetMapping("/{assetId}")
    public ResponseEntity<ImageResponse> retrieveImage(@PathVariable final Long assetId) {

        ImageResponse imageResponse = fileService.retrieveImage(assetId);

        return new ResponseEntity<>(imageResponse, HttpStatus.OK);
    }

    // @RoleCheck
    @PostMapping
    public ResponseEntity<ImageResponse> uploadAndRetrieveImage(@RequestBody final MultipartFile image) throws IOException {

        ImageResponse imageResponse = fileService.uploadImage(image);

        return new ResponseEntity<>(imageResponse, HttpStatus.OK);
    }

}
