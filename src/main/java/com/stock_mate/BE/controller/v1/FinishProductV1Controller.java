package com.stock_mate.BE.controller.v1;

import com.stock_mate.BE.dto.request.FinishProductRequest;
import com.stock_mate.BE.dto.response.FinishProductResponse;
import com.stock_mate.BE.dto.response.ResponseObject;
import com.stock_mate.BE.entity.FinishProductMedia;
import com.stock_mate.BE.enums.FinishProductCategory;
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

    @PostMapping
    public ResponseObject<FinishProductResponse> createFinishProduct(@RequestBody FinishProductRequest request){
        return ResponseObject.<FinishProductResponse>builder()
                .status(1000)
                .message("Tạo mới thành phẩm thành công")
                .data(finishProductService.createFinishProduct(request))
                .build();
    }

    @PutMapping
    public ResponseObject<FinishProductResponse> updateFinishProduct(
            @Parameter(description = "Finish Product ID", required = true)
            @RequestParam String fgID,

            @Parameter(description = "Name")
            @RequestParam(required = false) String name,

            @Parameter(description = "Description")
            @RequestParam(required = false) String description,

            @Parameter(description = "Category")
            @RequestParam(required = false) FinishProductCategory category,

            @Parameter(description = "Dimension")
            @RequestParam(required = false) String Dimension,

            @Parameter(description = "Status")
            @RequestParam(required = false) Integer status
    ){
        return ResponseObject.<FinishProductResponse>builder()
                .status(1000)
                .message("Cập nhật thành phẩm thành Công")
                .data(finishProductService.updateFinishProduct(fgID,name,description,category,Dimension,status))
                .build();
    }

    @DeleteMapping("/{finishProductId}")
    public ResponseObject<Boolean> deleteFinishProduct(@PathVariable String finishProductId){
        return ResponseObject.<Boolean>builder()
                .status(1000)
                .message("Xóa thành phầm thành công")
                .data(finishProductService.deleteFinishProduct(finishProductId))
                .build();
    }

    @GetMapping("/{finishProductId}")
    public ResponseObject<FinishProductResponse> getFinishProductById(@PathVariable String finishProductId) {
        FinishProductResponse finishProduct = finishProductService.getFinishProductById(finishProductId);
        return ResponseObject.<FinishProductResponse>builder()
                .status(1000)
                .data(finishProduct)
                .message("Lấy thông tin thành phẩm thành công")
                .build();
    }

    //Lây danh sách thanh phẩm hoàn thiện
    @GetMapping()
    public ResponseObject<Page<FinishProductResponse>> getAllFinishProducts(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "name,asc") String[] sort) {
        Page<FinishProductResponse> list = finishProductService.getAll(search, page, size, sort);

        return ResponseObject.<Page<FinishProductResponse>>builder()
                .status(1000)
                .data(list)
                .message("Lấy danh sách thành phẩm thành công")
                .build();
    }

    //Upload nhiều ảnh cho vật tư
    @PostMapping(value = "/{finishProductId}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload multiple images for a finish product")
    public ResponseObject<List<FinishProductMedia>> uploadFinishProductImages(
            @PathVariable String finishProductId,
            @RequestPart("files")
            @Parameter(
                    description = "Images file to upload",
                    content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
            MultipartFile[] files) throws Exception {

        List<MultipartFile> fileList = Arrays.asList(files);
        List<FinishProductMedia> savedMedia = finishProductService.updateFPImages(finishProductId, fileList);
        return ResponseObject.<List<FinishProductMedia>>builder()
                .status(1000)
                .data(savedMedia)
                .message("Các hình ảnh thuộc thành phẩm đã được tải lên thành công")
                .build();
    }

    //Xoá tất cả ảnh của thành phẩm
    @DeleteMapping("/{finishProductId}/images")
    public ResponseObject<Boolean> deleteFinishProductImages(@PathVariable String finishProductId) throws IOException {
        finishProductService.deleteFPImages(finishProductId);
        return ResponseObject.<Boolean>builder()
                .status(1000)
                .message("Các hình ảnh thuộc thành phẩm đã được xoá thành công")
                .build();
    }
}
