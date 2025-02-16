package rangiffler.service;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SpecificRequestDumperFilterTest {

    @Test
    void shouldCallDecorateFilterForMatchingUri(@Mock HttpServletRequest request,
                                                @Mock ServletResponse response,
                                                @Mock FilterChain chain,
                                                @Mock GenericFilter decorate) throws ServletException, IOException {
        final SpecificRequestDumperFilter filter = new SpecificRequestDumperFilter(decorate, "/login", "/oauth2/.*");

        when(request.getRequestURI()).thenReturn("/login");

        filter.doFilter(request, response, chain);

        verify(decorate, times(1)).doFilter(request, response, chain);
        verify(chain, times(0)).doFilter(request, response);
    }

    @Test
    void shouldSkipDecorateForNonHttpServletRequest(@Mock ServletRequest request,
                                                    @Mock ServletResponse response,
                                                    @Mock FilterChain chain,
                                                    @Mock GenericFilter decorate) throws ServletException, IOException {
        final SpecificRequestDumperFilter filter = new SpecificRequestDumperFilter(decorate, "/login", "/oauth2/.*");

        filter.doFilter(request, response, chain);

        verify(decorate, times(0)).doFilter(request, response, chain);
        verify(chain, times(1)).doFilter(request, response);
    }

    @Test
    void shouldSkipDecorateForNonPatternUri(@Mock HttpServletRequest request,
                                            @Mock ServletResponse response,
                                            @Mock FilterChain chain,
                                            @Mock GenericFilter decorate) throws ServletException, IOException {
        final SpecificRequestDumperFilter filter = new SpecificRequestDumperFilter(decorate, "/login", "/oauth2/.*");

        when(request.getRequestURI()).thenReturn("/logout");

        filter.doFilter(request, response, chain);

        verify(decorate, times(0)).doFilter(request, response, chain);
        verify(chain, times(1)).doFilter(request, response);
    }

    @Test
    void shouldCallChainWithoutPatterns(@Mock HttpServletRequest request,
                                        @Mock ServletResponse response,
                                        @Mock FilterChain chain,
                                        @Mock GenericFilter decorate) throws ServletException, IOException {
        final SpecificRequestDumperFilter filter = new SpecificRequestDumperFilter(decorate);

        lenient().when(request.getRequestURI()).thenReturn("/login");

        filter.doFilter(request, response, chain);

        verify(decorate, times(0)).doFilter(request, response, chain);
        verify(chain, times(1)).doFilter(request, response);
    }


    @Test
    void shouldDestroyFilter(@Mock GenericFilter decorate) {
        final SpecificRequestDumperFilter filter = new SpecificRequestDumperFilter(decorate);

        filter.destroy();

        verify(decorate, times(1)).destroy();
    }
}
