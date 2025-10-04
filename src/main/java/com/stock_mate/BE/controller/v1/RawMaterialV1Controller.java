package com.stock_mate.BE.controller.v1;

import com.stock_mate.BE.dto.response.RawMaterialResponse;
import com.stock_mate.BE.dto.response.ResponseObject;
import com.stock_mate.BE.entity.RawMaterial;
import com.stock_mate.BE.entity.RawMaterialMedia;
import com.stock_mate.BE.repository.RawMaterialMediaRepository;
import com.stock_mate.BE.service.CloudinaryService;
import com.stock_mate.BE.service.RawMaterialMediaService;
import com.stock_mate.BE.service.RawMaterialService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Tag(name = "Raw Material", description ="API Vật tư, Nguyên vật liệu")
@RestController
@RequestMapping("/v1/raw-materials")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RawMaterialV1Controller {
    @Autowired
    RawMaterialService rawMaterialService;


    //Lây danh sách vật tư
    @GetMapping()
    public ResponseObject<Page<RawMaterialResponse>> getAllRawMaterials(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "name,asc") String[] sort) {

        Page<RawMaterialResponse> list = rawMaterialService.getAllRawMaterials(search, page, size, sort);

        return ResponseObject.<Page<RawMaterialResponse>>builder()
                .status(1000)
                .data(list)
                .message("Lấy danh sách vật tư thành công")
                .build();
    }

    //Upload nhiều ảnh cho vật tư
    @PostMapping(value = "/{materialId}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload multiple images for a raw material")
    public ResponseObject uploadRawMaterialImages(
            @PathVariable String materialId,
            @RequestPart("files")
            @Parameter(
                    description = "Images file to upload",
                    content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
            MultipartFile[] files) throws Exception {

        List<MultipartFile> fileList = Arrays.asList(files);
        List<RawMaterialMedia> savedMedia = rawMaterialService.updateRMImages(materialId, fileList);
        return ResponseObject.builder()
                .status(1000)
                .data(savedMedia)
                .message("Raw material images uploaded successfully")
                .build();
    }

    //Xoá tất cả ảnh của vật tư
    @DeleteMapping("/{materialId}/images")
    public ResponseObject deleteRawMaterialImages(@PathVariable String materialId) throws IOException {
        rawMaterialService.deleteRMImages(materialId);
        return ResponseObject.builder()
                .status(1000)
                .message("All images for raw material deleted successfully")
                .build();
    }

}
