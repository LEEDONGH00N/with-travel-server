package com.arom.with_travel.global.oauth2.handler;

import com.arom.with_travel.domain.member.Member;
import com.arom.with_travel.domain.member.service.MemberService;
import com.arom.with_travel.global.jwt.domain.RefreshToken;
import com.arom.with_travel.global.jwt.repository.RefreshTokenRepository;
import com.arom.with_travel.global.jwt.service.TokenProvider;
import com.arom.with_travel.global.oauth2.dto.CustomOAuth2User;
import com.arom.with_travel.global.oauth2.util.CookieUtil;
import com.arom.with_travel.global.oauth2.util.OAuth2AuthorizationRequestBasedOnCookieRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.time.Duration;
import java.util.Optional;

// 차현철
@RequiredArgsConstructor
@Component
@Log4j2
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    public static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";
    public static final Duration REFRESH_TOKEN_DURATION = Duration.ofDays(14);
    public static final Duration ACCESS_TOKEN_DURATION = Duration.ofDays(1);

    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final OAuth2AuthorizationRequestBasedOnCookieRepository authorizationRequestRepository;
    private final MemberService memberService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        CustomOAuth2User tmpMember = (CustomOAuth2User) authentication.getPrincipal();
        if(tmpMember.getRole().equals(Member.Role.GUEST)){
            redirectToSignupWithUserInfo(request,
                    response,
                    tmpMember
            );
            return;
        }

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        
        Member member = memberService.getUserByLoginEmailOrElseThrow(oAuth2User.getAttributes().get("email").toString());

        // 리프레시 토큰 생성 -> 저장 -> 쿠키에 저장
        String refreshToken = tokenProvider.generateToken(member, REFRESH_TOKEN_DURATION);
        saveRefreshToken(member.getId(), refreshToken);
        addRefreshTokenToCookie(request, response, refreshToken);

        // 액세스 토큰 생성 -> 패스에 액세스 토큰 추가
        String accessToken = tokenProvider.generateToken(member, ACCESS_TOKEN_DURATION);
        response.setHeader("Authorization", "Bearer " + accessToken);

        // 인증관련 설정 값, 쿠키 제거
        clearAuthenticationAttributes(request, response);

        // 리다이렉트
        Optional<String> redirectUri = CookieUtil.getCookie(request, "redirect_uri")
                .map(Cookie::getValue);
        String targetUrl = "http://localhost:8080/login/oauth2/code/kakao?token=" + accessToken;

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    // 리프레시 토큰 DB에 저장
    private void saveRefreshToken(Long userId, String newRefreshToken) {
        RefreshToken refreshToken = refreshTokenRepository.findByMemberId(userId)
                .map(entity -> entity.update(newRefreshToken))
                .orElse(RefreshToken.create(userId, newRefreshToken));

        refreshTokenRepository.save(refreshToken);
    }

    private void addRefreshTokenToCookie(HttpServletRequest request, HttpServletResponse response, String refreshToken) {
        int cookieMaxAge = (int) REFRESH_TOKEN_DURATION.toSeconds();

        CookieUtil.deleteCookie(request, response, REFRESH_TOKEN_COOKIE_NAME);
        CookieUtil.addCookie(response, REFRESH_TOKEN_COOKIE_NAME, refreshToken, cookieMaxAge);
    }

    // 인증 관련 설정 값 제거
    private void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        authorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }

    private void redirectToSignupWithUserInfo(HttpServletRequest request,
                                              HttpServletResponse response,
                                              CustomOAuth2User member) throws IOException {
        log.info("최초 로그인인 경우 추가 정보 입력을 위한 회원가입 페이지로 리다이렉트");
        //response.addHeader(JWT_REFRESH_TOKEN_COOKIE_NAME, JWT_ACCESS_TOKEN_TYPE + accessToken);
        String redirectURL = createRedirectUri(member);

        CustomOAuth2User newPrincipal = new CustomOAuth2User(member.getOAuth2Response(), Member.Role.GUEST);

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(newPrincipal, null, newPrincipal.getAuthorities());

        //SecurityContextHolder.getContext().setAuthentication(authentication);

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        HttpSession session = request.getSession(true);
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);

        getRedirectStrategy().sendRedirect(request, response, redirectURL);
    }

    private String createRedirectUri(CustomOAuth2User member) {
        return UriComponentsBuilder.fromUriString("http://localhost:8080/signup/check").toUriString(); //
//                .queryParam("email", member.getEmail())
//                .queryParam("name", member.getName())
//                .build()
//                .encode(StandardCharsets.UTF_8)
//                .toUriString();
    }
}