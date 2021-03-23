package org.nozomi.jikkenkichi.machikouba.fliter;

import com.alibaba.fastjson.JSON;
import org.nozomi.jikkenkichi.machikouba.pojo.LocalConfig;
import org.nozomi.jikkenkichi.machikouba.pojo.RequestInfo;
import org.nozomi.jikkenkichi.machikouba.pojo.ResultInfo;
import org.nozomi.jikkenkichi.machikouba.util.DebugTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Unification of the output format of the framework
 */
@WebFilter(filterName = "ProcessFilter", urlPatterns = {"/jkkt/*"})
public class ProcessFilter extends OncePerRequestFilter {
    private static ThreadLocal<RequestInfo> REQUEST_INFO = new ThreadLocal<>();

    @Autowired
    LocalConfig localConfig;

    public static RequestInfo getRequestInfo() {
        return REQUEST_INFO.get();
    }

    /**
     * auto log request and response
     * auto package response to ResultInfo
     *
     * @param httpServletRequest
     * @param httpServletResponse
     * @param filterChain
     * @throws IOException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws IOException {
        RequestWrapper requestWrapper = new RequestWrapper(httpServletRequest);
        ResponseWrapper responseWrapper = new ResponseWrapper(httpServletResponse);
        //record some info for picking correct resource
        RequestInfo requestInfo = recordRequestInfo(requestWrapper);
        DebugTool.print(String.format("req id:[%s] info:[%s]", requestInfo.getRequestId(), JSON.toJSONString(REQUEST_INFO.get())));

        String responseStr = "";
        try {
            filterChain.doFilter(requestWrapper, responseWrapper);
            if (httpServletResponse.getStatus() == 200) {
                responseStr = checkResponseFormat(responseWrapper);
            }
        } catch (Exception e) {
            DebugTool.recordAndSkip(e);
            responseStr = JSON.toJSONString(new ResultInfo<>(-1, e.getMessage(), null));
        } finally {
            //remove ThreadLocal to avoid GC and thread pool problem
            REQUEST_INFO.remove();
            if (httpServletResponse.getStatus() == 200) {
                DebugTool.print(String.format("req id:[%s] response:[%s]", requestInfo.getRequestId(), responseStr));
                rewriteResponse(httpServletResponse, responseStr);
            } else {
                DebugTool.print(String.format("req id:[%s] response code:[%d]", requestInfo.getRequestId(), httpServletResponse.getStatus()));
            }
        }
    }

    RequestInfo recordRequestInfo(RequestWrapper request) throws IOException {
        RequestInfo ri = new RequestInfo();
        ri.setMethod(request.getMethod());
        ri.setPath(request.getRequestURI());
        if ("GET".equals(ri.getMethod())) {
            ri.setQueryParam(request.getQueryString());
        }
        if ("POST".equals(ri.getMethod())) {
            ri.setRequestBody(request.getBody());
        }
        ri.setLocale(request.getHeader("locale"));
        REQUEST_INFO.set(ri);
        return ri;
    }

    String checkResponseFormat(ResponseWrapper wrapper) throws UnsupportedEncodingException {
        String responseStr = new String(wrapper.toByteArray(), localConfig.getCharset());
        try {
            return JSON.toJSONString(new ResultInfo<>(JSON.parse(responseStr)));
        } catch (Exception e) {
            return JSON.toJSONString(new ResultInfo<>(responseStr));
        }
    }

    void rewriteResponse(HttpServletResponse httpServletResponse, String responseStr) throws IOException {
        httpServletResponse.setContentType(String.format("application/json;charset=%s", localConfig.getCharset()));
        byte[] bs = responseStr.getBytes(localConfig.getCharset());
        httpServletResponse.setContentLength(bs.length);
        httpServletResponse.getOutputStream().write(bs);
    }

}