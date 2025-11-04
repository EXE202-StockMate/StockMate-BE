package com.stock_mate.BE.service;


import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.stock_mate.BE.dto.request.AuthenticationRequest;
import com.stock_mate.BE.dto.request.ResetPasswordRequest;
import com.stock_mate.BE.dto.response.AuthenticationResponse;
import com.stock_mate.BE.entity.User;
import com.stock_mate.BE.exception.AppException;
import com.stock_mate.BE.exception.ErrorCode;
import com.stock_mate.BE.mapper.UserMapper;
import com.stock_mate.BE.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;


import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService{

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserMapper userMapper;

    private final PasswordEncoder passwordEncoder;

    @NonFinal
    @Value("${jwt.signerKey}") //Đọc từ file application.yaml
    protected String SIGNER_KEY;

    @NonFinal
    @Value("${jwt.valid-duration}") //Đọc từ file application.yaml
    protected long VALID_DURATION;

    @NonFinal
    @Value("${jwt.refreshable-duration}") //Đọc từ file application.yaml
    protected long REFRESHABLE_DURATION;



    //--------------------------------------- AUTHENTICATION --------------------------------------------------
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        User user = userRepository.findByUsername(request.getUsername());
        if (user == null) {
            throw new AppException(ErrorCode.USER_NOT_FOUND, "Không tìm thấy tài khoản");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.INVALID_CREDENTIALS, "Mật khẩu không đúng");
        }
        String token;
        token = generateToken(user);
        return AuthenticationResponse.builder()
                .authenticated(true)
                .token(token)
                .user(userMapper.toDto(user))
                .build();
    }

//    //--------------------------------------- LOGOUT -----------------------------------------------------------
//    public void logout(LogoutRequest request) throws ParseException, JOSEException {
//        try {
//            var signToken = verifyToken(request.getToken(), true);
//
//            String jti = signToken.getJWTClaimsSet().getJWTID();
//            Date expiryTime = signToken.getJWTClaimsSet().getExpirationTime();
//
//            InvalidatedToken invalidatedToken = InvalidatedToken.builder()
//                    .id(jti)
//                    .expiryTime(expiryTime)
//                    .build();
//
//            invalidatedTokenRepository.save(invalidatedToken);
//        } catch (AppException e){
//            log.info("Token is already expired");
//        }
//    }

    //-----------------------------------VERIFY---------------------------------------
    private void verifyToken(String token, boolean isRefresh) throws ParseException, JOSEException {
        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());

        SignedJWT signedJWT = SignedJWT.parse(token);

        Date expiryTime = (isRefresh)
                ? new Date(signedJWT.getJWTClaimsSet().getIssueTime()
                .toInstant().plus(REFRESHABLE_DURATION, ChronoUnit.SECONDS).toEpochMilli())
                :
                signedJWT.getJWTClaimsSet().getExpirationTime();

        var verified = signedJWT.verify(verifier);
        if(!(verified && expiryTime.after(new Date())))
            throw new AppException(ErrorCode.UNAUTHENTICATED);
    }

    private String generateToken(Object userEntity) {
        String username = "";
        String userId = "";
        String scope = "";

        if (userEntity instanceof User user) {
            username = user.getUsername();
            userId = user.getUserID();
            scope = buildScope(user);
        }

        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .subject(username)
                .claim("userID", userId)
                .claim("scope", scope)
                .jwtID(UUID.randomUUID().toString())
                .issuer("stockmate")
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(VALID_DURATION, ChronoUnit.SECONDS).toEpochMilli()
                ))
                .subject(username) // name của biến authentication
                .claim("userID", userId)
                .claim("scope", scope)
                .issuer("stockmate")
                .issueTime(new Date())
                .expirationTime(Date.from(Instant.now().plus(1,
                        ChronoUnit.HOURS)))
                .build();
        try {
            SignedJWT signedJWT = new SignedJWT(
                    new JWSHeader(JWSAlgorithm.HS512),
                    claims
            );
            signedJWT.sign(new MACSigner(SIGNER_KEY.getBytes(StandardCharsets.UTF_8)));
            return signedJWT.serialize(); // SignedJWT trả về chuỗi JWT chuẩn
        } catch (JOSEException e) {
            log.error("Token generation failed", e);
            throw new RuntimeException(e);
        }
    }

    //build ra scope - chứa role và các permission
    private String buildScope(Object userEntity) {
        if (userEntity instanceof User user) {
            return user.getRole().getName();
        }
        return "";
    }

    public boolean introspect(String token) throws JOSEException, ParseException {
        boolean isValid = true;;
        try {
            verifyToken(token, false);
        } catch (AppException e){
            isValid = false;
        }
        return isValid;
    }

    //reset password với token
    public boolean resetPassword(ResetPasswordRequest request) throws ParseException, JOSEException {
        String token = request.getAccessToken();
        String newPassword = request.getNewPassword();
        String confirmPassword = request.getConfirmPassword();

        //valid token
        if (!introspect(request.getAccessToken()))
            throw new AppException(ErrorCode.INVALID_CREDENTIALS);

        // Validate password confirmation
        if (!newPassword.equals(confirmPassword)) {
            throw new AppException(ErrorCode.PASSWORD_MISMATCH);
        }

        String userID = (String) getClaim(request.getAccessToken(), "userID");

        // Tìm user và update password
        User user = userService.findById(userID);

        if (user != null) {
            // Update customer password
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
            log.info("Password reset successfully for customer: {}", user.getUsername());
        } else {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }
        return true;
    }

    //Parse token và verify signature
    public JWTClaimsSet getClaims(String token) throws ParseException, JOSEException {
        SignedJWT signedJWT = SignedJWT.parse(token);
        MACVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());

        if (!signedJWT.verify(verifier)) {
            throw new JOSEException("Invalid JWT signature");
        }

        return signedJWT.getJWTClaimsSet();
    }

    // Lấy subject từ token
    public String getSubject(String token) throws ParseException, JOSEException {
        return getClaims(token).getSubject();
    }

    //Lấy claim bất kỳ theo tên
    public Object getClaim(String token, String claimName) throws ParseException, JOSEException {
        JWTClaimsSet claims = getClaims(token);
        return claims.getClaim(claimName);
    }
}
