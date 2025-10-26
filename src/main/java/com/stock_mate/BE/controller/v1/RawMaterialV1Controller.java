package com.stock_mate.BE.controller.v1;

import com.stock_mate.BE.dto.request.RawMaterialRequest;
import com.stock_mate.BE.dto.request.RawMaterialUpdateRequest;
import com.stock_mate.BE.dto.response.RawMaterialResponse;
import com.stock_mate.BE.dto.response.ResponseObject;
import com.stock_mate.BE.entity.RawMaterialMedia;
import com.stock_mate.BE.enums.RawMaterialCategory;
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

@Tag(name = "Raw Material", description = "API Vật tư, Nguyên vật liệu")
@RestController
@RequestMapping("/v1/raw-materials")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RawMaterialV1Controller {
    @Autowired
    RawMaterialService rawMaterialService;

    @PostMapping
    public ResponseObject<RawMaterialResponse> createRawMaterial(@RequestBody RawMaterialRequest rawMaterialRequest) {
        return ResponseObject.<RawMaterialResponse>builder()
                .status(1000)
                .message("Tạo vật tư mới thành công")
                .data(rawMaterialService.createRawMaterial(rawMaterialRequest))
                .build();
    }

    @PutMapping
    public ResponseObject<RawMaterialResponse> updateRawMaterial(
            @Parameter(description = "RawMaterialID", required = true)
            @RequestParam String rmID,

            @Parameter(description = "Name")
            @RequestParam(required = false) String name,

            @Parameter(description = "Code")
            @RequestParam(required = false) String code,

            @Parameter(description = "Description")
            @RequestParam(required = false) String description,

            @Parameter(description = "Category")
            @RequestParam(required = false)RawMaterialCategory category,

            @Parameter(description = "Dimension")
            @RequestParam(required = false) String dimension,

            @Parameter(description = "Thickness")
            @RequestParam(required = false) Integer thickness,

            @Parameter(description = "Status")
            @RequestParam(required = false) Integer status
    ) {
        RawMaterialUpdateRequest up = new RawMaterialUpdateRequest();
        up.setName(name);
        up.setCode(code);
        up.setDescription(description);
        up.setCategory(category);
        up.setDimension(dimension);
        up.setThickness(thickness);
        up.setStatus(status);
        return ResponseObject.<RawMaterialResponse>builder()
                .status(1000)
                .message("Cập nhật vật tư thành công")
                .data(rawMaterialService.updateRawMaterial(up, rmID))
                .build();
    }

    //Lây danh sách vật tư
    @GetMapping()
    public ResponseObject<Page<RawMaterialResponse>> getAllRawMaterials(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "name,asc") String[] sort) {

        Page<RawMaterialResponse> list = rawMaterialService.getAll(search, page, size, sort);

        return ResponseObject.<Page<RawMaterialResponse>>builder()
                .status(1000)
                .data(list)
                .message("Lấy danh sách vật tư thành công")
                .build();
    }

    @GetMapping("/{materialId}")
    public ResponseObject<RawMaterialResponse> getRawMaterial(@PathVariable String materialId) {
        return ResponseObject.<RawMaterialResponse>builder()
                .status(1000)
                .message("Lấy vật tư thành công")
                .data(rawMaterialService.findById(materialId))
                .build();
    }

    //Upload nhiều ảnh cho vật tư
    @PostMapping(value = "/{materialId}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload multiple images for a raw material")
    public ResponseObject<List<RawMaterialMedia>> uploadRawMaterialImages(
            @PathVariable String materialId,
            @RequestPart("files")
            @Parameter(
                    description = "Images file to upload",
                    content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
            MultipartFile[] files) throws Exception {

        List<MultipartFile> fileList = Arrays.asList(files);
        List<RawMaterialMedia> savedMedia = rawMaterialService.updateRMImages(materialId, fileList);
        return ResponseObject.<List<RawMaterialMedia>>builder()
                .status(1000)
                .data(savedMedia)
                .message("Raw material images uploaded successfully")
                .build();
    }

    //Xoá tất cả ảnh của vật tư
    @DeleteMapping("/{materialId}/images")
    public ResponseObject<Boolean> deleteRawMaterialImages(@PathVariable String materialId) throws IOException {
        rawMaterialService.deleteRMImages(materialId);
        return ResponseObject.<Boolean>builder()
                .status(1000)
                .message("All images for raw material deleted successfully")
                .build();
    }

    @DeleteMapping
    public ResponseObject<Boolean> deleteAllRawMaterialImages(@RequestParam(required = true) String rmID) {
        return ResponseObject.<Boolean>builder()
                .status(1000)
                .message("Xóa vật tư thành công")
                .data(rawMaterialService.deleteRawMaterial(rmID))
                .build();
    }

}
