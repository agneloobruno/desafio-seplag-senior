package com.seplag.desafio.backend.service;

import io.minio.BucketExistsArgs;
import io.minio.GetPresignedObjectUrlArgs; // Import Novo
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.http.Method; // Import Novo
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import java.util.concurrent.TimeUnit; // Import Novo

@Service
@RequiredArgsConstructor
public class MinioService {

    private final MinioClient minioClient;

    @Value("${minio.bucket-name}")
    private String bucketName;

    @Value("${minio.public-url:http://localhost:9000}")
    private String minioPublicUrl;

    public String upload(MultipartFile file) {
        package com.seplag.desafio.backend.service;

        import io.minio.BucketExistsArgs;
        import io.minio.GetPresignedObjectUrlArgs;
        import io.minio.MakeBucketArgs;
        import io.minio.MinioClient;
        import io.minio.PutObjectArgs;
        import io.minio.http.Method;
        import lombok.RequiredArgsConstructor;
        import org.springframework.beans.factory.annotation.Value;
        import org.springframework.stereotype.Service;
        import org.springframework.web.multipart.MultipartFile;

        import java.io.InputStream;
        import java.util.UUID;
        import java.util.concurrent.TimeUnit;

        @Service
        @RequiredArgsConstructor
        public class MinioService {

            private final MinioClient minioClient;

            @Value("${minio.bucket-name}")
            private String bucketName;

            @Value("${minio.public-url:http://localhost:9000}")
            private String minioPublicUrl;

            public String upload(MultipartFile file) {
                try {
                    boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
                    if (!found) {
                        minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
                    }

                    String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
                    InputStream inputStream = file.getInputStream();
                    minioClient.putObject(
                            PutObjectArgs.builder()
                                    .bucket(bucketName)
                                    .object(fileName)
                                    .stream(inputStream, file.getSize(), -1)
                                    .contentType(file.getContentType())
                                    .build()
                    );

                    return fileName;

                } catch (Exception e) {
                    throw new RuntimeException("Erro ao fazer upload da imagem: " + e.getMessage());
                }
            }

            // --- NOVO MÉTODO: Gerar URL de visualização ---
            public String getUrl(String fileName) {
                try {
                    // Como o bucket é público (definido no docker-compose com mc anonymous set public),
                    // não precisamos de URL assinada. Retornamos URL pública direta.
                    return String.format("%s/%s/%s", minioPublicUrl, bucketName, fileName);
                } catch (Exception e) {
                    // Se der erro, retorna null ou loga o erro
                    e.printStackTrace();
                    return null;
                }
            }

        }