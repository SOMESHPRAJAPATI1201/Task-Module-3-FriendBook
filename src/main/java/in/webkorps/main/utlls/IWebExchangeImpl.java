package in.webkorps.main.utlls;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.thymeleaf.web.IWebApplication;
import org.thymeleaf.web.IWebExchange;
import org.thymeleaf.web.IWebRequest;
import org.thymeleaf.web.IWebSession;

import java.io.InputStream;
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

public class IWebExchangeImpl implements IWebExchange {

    private final HttpServletRequest request;
    private final HttpServletResponse response;

    public IWebExchangeImpl(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
    }


    @Override
    public IWebRequest getRequest() {
        return new IWebRequestImpl(request);
    }


    public class IWebRequestImpl implements IWebRequest {

        private final HttpServletRequest request;

        public IWebRequestImpl(HttpServletRequest request) {
            this.request = request;
        }

        @Override
        public String getMethod() {
            return request.getMethod();
        }

        @Override
        public String getScheme() {
            return request.getScheme();
        }

        @Override
        public String getServerName() {
            return request.getServerName();
        }

        @Override
        public Integer getServerPort() {
            return request.getServerPort();
        }

        @Override
        public String getApplicationPath() {
            return request.getContextPath();
        }

        @Override
        public String getPathWithinApplication() {
            return request.getRequestURI().substring(request.getContextPath().length());
        }

        @Override
        public String getQueryString() {
            return request.getQueryString();
        }

        @Override
        public boolean containsHeader(String name) {
            return request.getHeader(name) != null;
        }

        @Override
        public int getHeaderCount() {
            return Collections.list(request.getHeaderNames()).size();
        }

        @Override
        public Set<String> getAllHeaderNames() {
            return new HashSet<>(Collections.list(request.getHeaderNames()));
        }


        @Override
        public Map<String, String[]> getHeaderMap() {
            return Collections.list(request.getHeaderNames()).stream()
                    .collect(Collectors.toMap(
                            name -> name,
                            name -> Collections.list(request.getHeaders(name)).toArray(String[]::new)
                    ));
        }

        @Override
        public String[] getHeaderValues(String name) {
            return Collections.list(request.getHeaders(name)).toArray(String[]::new);
        }

        @Override
        public boolean containsParameter(String name) {
            return request.getParameter(name) != null;
        }

        @Override
        public int getParameterCount() {
            return request.getParameterMap().size();
        }

        @Override
        public Set<String> getAllParameterNames() {
            return request.getParameterMap().keySet();
        }

        @Override
        public Map<String, String[]> getParameterMap() {
            return request.getParameterMap();
        }

        @Override
        public String[] getParameterValues(String name) {
            return request.getParameterValues(name);
        }

        @Override
        public boolean containsCookie(String name) {
            return Arrays.stream(Optional.ofNullable(request.getCookies()).orElse(new Cookie[0]))
                    .anyMatch(cookie -> cookie.getName().equals(name));
        }

        @Override
        public int getCookieCount() {
            return Optional.ofNullable(request.getCookies()).map(cookies -> cookies.length).orElse(0);
        }

        @Override
        public Set<String> getAllCookieNames() {
            return Arrays.stream(Optional.ofNullable(request.getCookies()).orElse(new Cookie[0]))
                    .map(Cookie::getName)
                    .collect(Collectors.toSet());
        }

        @Override
        public Map<String, String[]> getCookieMap() {
            return Arrays.stream(Optional.ofNullable(request.getCookies()).orElse(new Cookie[0]))
                    .collect(Collectors.toMap(Cookie::getName, cookie -> new String[]{cookie.getValue()}));
        }

        @Override
        public String[] getCookieValues(String name) {
            return Arrays.stream(Optional.ofNullable(request.getCookies()).orElse(new Cookie[0]))
                    .filter(cookie -> cookie.getName().equals(name))
                    .map(Cookie::getValue)
                    .toArray(String[]::new);
        }
    }


    @Override
    public IWebSession getSession() {
        return new IWebSessionImpl(request.getSession()); // Custom session implementation.
    }

    @Override
    public IWebApplication getApplication() {
        return new IWebApplicationImpl(request.getServletContext());
    }


    @Override
    public Principal getPrincipal() {
        return request.getUserPrincipal(); // Get the authenticated user's principal.
    }

    @Override
    public Locale getLocale() {
        return request.getLocale(); // Return the preferred locale of the request.
    }

    @Override
    public String getContentType() {
        return request.getContentType() != null ? request.getContentType() : "";
    }

    @Override
    public String getCharacterEncoding() {
        return request.getCharacterEncoding() != null ? request.getCharacterEncoding() : "UTF-8";
    }

    @Override
    public boolean containsAttribute(String name) {
        return request.getAttribute(name) != null;
    }

    @Override
    public int getAttributeCount() {
        return Collections.list(request.getAttributeNames()).size();
    }

    @Override
    public Set<String> getAllAttributeNames() {
        return Set.copyOf(Collections.list(request.getAttributeNames()));
    }

    @Override
    public Map<String, Object> getAttributeMap() {
        Map<String, Object> attributeMap = new HashMap<>();
        for (String name : Collections.list(request.getAttributeNames())) {
            attributeMap.put(name, request.getAttribute(name));
        }
        return attributeMap;
    }

    @Override
    public Object getAttributeValue(String name) {
        return request.getAttribute(name);
    }

    @Override
    public void setAttributeValue(String name, Object value) {
        request.setAttribute(name, value);
    }

    @Override
    public void removeAttribute(String name) {
        request.removeAttribute(name);
    }

    @Override
    public String transformURL(String url) {
        return request.getContextPath() + url; // Prefix with the application context path.
    }

    public class IWebSessionImpl implements IWebSession {

        private final HttpSession session;

        // Constructor to inject the HttpSession
        public IWebSessionImpl(HttpSession session) {
            this.session = session;
        }

        @Override
        public boolean exists() {
            return session != null;
        }

        @Override
        public boolean containsAttribute(String name) {
            return session.getAttribute(name) != null;
        }

        @Override
        public int getAttributeCount() {
            return Collections.list(session.getAttributeNames()).size();
        }

        @Override
        public Set<String> getAllAttributeNames() {
            return Set.copyOf(Collections.list(session.getAttributeNames()));
        }

        @Override
        public Map<String, Object> getAttributeMap() {
            Map<String, Object> attributeMap = new HashMap<>();
            for (String name : Collections.list(session.getAttributeNames())) {
                attributeMap.put(name, session.getAttribute(name));
            }
            return attributeMap;
        }

        @Override
        public Object getAttributeValue(String name) {
            return session.getAttribute(name);
        }

        @Override
        public void setAttributeValue(String name, Object value) {
            session.setAttribute(name, value);
        }

        @Override
        public void removeAttribute(String name) {
            session.removeAttribute(name);
        }

}

    public class IWebApplicationImpl implements IWebApplication {

        private final ServletContext servletContext;
        private final Map<String, Object> attributes = new HashMap<>();

        public IWebApplicationImpl(ServletContext servletContext) {
            this.servletContext = servletContext;
        }

        @Override
        public boolean containsAttribute(String name) {
            return attributes.containsKey(name);
        }

        @Override
        public int getAttributeCount() {
            return attributes.size();
        }

        @Override
        public Set<String> getAllAttributeNames() {
            return attributes.keySet();
        }

        @Override
        public Map<String, Object> getAttributeMap() {
            return Collections.unmodifiableMap(attributes);
        }

        @Override
        public Object getAttributeValue(String name) {
            return attributes.get(name);
        }

        @Override
        public void setAttributeValue(String name, Object value) {
            attributes.put(name, value);
        }

        @Override
        public void removeAttribute(String name) {
            attributes.remove(name);
        }

        @Override
        public boolean resourceExists(String path) {
            // Check if a resource exists in the web application using the servlet context.
            return servletContext.getResourceAsStream(path) != null;
        }

        @Override
        public InputStream getResourceAsStream(String path) {
            return servletContext.getResourceAsStream(path);
        }
    }

}
