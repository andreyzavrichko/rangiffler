package rangiffler.service.cors;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.web.csrf.CsrfToken;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CookieCsrfFilterTest {

  private final CookieCsrfFilter cookieCsrfFilter = new CookieCsrfFilter();

  @Test
  void doFilterInternal(@Mock HttpServletRequest request,
      @Mock HttpServletResponse response,
      @Mock FilterChain filterChain,
      @Mock CsrfToken csrfToken) throws ServletException, IOException {
    final String headerName = "csrf-header-name";
    final String headerValue = "12345";

    when(csrfToken.getHeaderName())
        .thenReturn(headerName);
    when(csrfToken.getToken())
        .thenReturn(headerValue);

    when(request.getAttribute(eq(CsrfToken.class.getName())))
        .thenReturn(csrfToken);

    cookieCsrfFilter.doFilterInternal(request, response, filterChain);

    verify(response, times(1)).setHeader(headerName, headerValue);
    verify(filterChain, times(1)).doFilter(eq(request), eq(response));
  }
}