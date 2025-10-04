package com.stock_mate.BE.controller.v1;

import com.stock_mate.BE.dto.response.FinishProductResponse;
import com.stock_mate.BE.dto.response.ResponseObject;
import com.stock_mate.BE.entity.FinishProductMedia;
import com.stock_mate.BE.service.FinishProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Tag(name = "Finish Product", description ="API Quản lý sản phẩm hoàn thiện")
@RestController
@RequestMapping("/v1/finish-products")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FinishProductV1Controller {
    @Autowired
    FinishProductService finishProductService;


    //Lây danh sách thanh phẩm hoàn thiện
    @GetMapping()
    public ResponseObject<Page<FinishProductResponse>> getAllFinishProducts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "name,asc") String[] sort) {

        Page<FinishProductResponse> list = finishProductService.getAllFinishProducts(search, page, size, sort);

        return ResponseObject.<Page<FinishProductResponse>>builder()
                .status(1000)
                .data(list)
                .message("Lấy danh sách thành phẩm thành công")
                .build();
    }

    //Upload nhiều ảnh cho vật tư
    @PostMapping(value = "/{finishProductId}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload multiple images for a finish product")
    public ResponseObject uploadFinishProductImages(
            @PathVariable String finishProductId,
            @RequestPart("files")
            @Parameter(
                    description = "Images file to upload",
                    content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
            MultipartFile[] files) throws Exception {

        List<MultipartFile> fileList = Arrays.asList(files);
        List<FinishProductMedia> savedMedia = finishProductService.updateFPImages(finishProductId, fileList);
        return ResponseObject.builder()
                .status(1000)
                .data(savedMedia)
                .message("Các hình ảnh thuộc thành phẩm đã được tải lên thành công")
                .build();
    }

    //Xoá tất cả ảnh của thành phẩm
    @DeleteMapping("/{finishProductId}/images")
    public ResponseObject deleteFinishProductImages(@PathVariable String finishProductId) throws IOException {
        finishProductService.deleteFPImages(finishProductId);
        return ResponseObject.builder()
                .status(1000)
                .message("Các hình ảnh thuộc thành phẩm đã được xoá thành công")
                .build();
    }
}
