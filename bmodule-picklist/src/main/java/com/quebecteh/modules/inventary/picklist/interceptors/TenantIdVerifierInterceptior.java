package com.quebecteh.modules.inventary.picklist.interceptors;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import com.quebecteh.modules.inventary.picklist.service.PickListTenantService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@RequiredArgsConstructor
@Component
public class TenantIdVerifierInterceptior implements HandlerInterceptor {

	
	
	//private final PickListUserAuth auth;
	
	private final PickListTenantService tenantService;
	

	@SneakyThrows
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler){
		
		if (! (handler instanceof HandlerMethod) ) { 
			return true; 
		}
		
		HandlerMethod method = (HandlerMethod) handler;
		if ( !(method.getMethod().isAnnotationPresent(RequiredTenatantId.class) || 
	                method.getBeanType().isAnnotationPresent(RequiredTenatantId.class)) ) {
            return true; 
        }
    	
    	String tenantId = "";
    	String context = request.getContextPath();
    	String uri = request.getRequestURI();
    	uri = uri.replace(context, "");
        String[] parts = uri.split("/");
        if (parts.length > 1) {
            tenantId = parts[1].toLowerCase(); 
        }
        
       
    	if (tenantService.countByTenantId(tenantId) == 0 ) {
    		request.getRequestDispatcher("/error/tenant/"+tenantId).forward(request, response);
    		return false;
    	}
		
        return true;
    }
}