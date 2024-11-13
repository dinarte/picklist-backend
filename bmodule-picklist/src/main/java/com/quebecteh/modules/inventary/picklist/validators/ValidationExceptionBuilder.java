package com.quebecteh.modules.inventary.picklist.validators;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.quebecteh.commons.reflaction.utils.StackTraceUtils;

public class ValidationExceptionBuilder {

		
		private static List<ValidationError> errors = new ArrayList<ValidationError>();
	
		@SuppressWarnings("all")
		public static ValidationExceptionBuilder add(Object instance, String fieldName,  String message) {
			
			
			try {
				//instance.getClass().getField(fieldName);
				String getMethodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
				Method method = instance.getClass().getMethod(getMethodName);				
				String objectName = instance.getClass().getSimpleName();
				objectName = objectName.substring(0, 1).toUpperCase() + objectName.substring(1);
				String messageId = message.toLowerCase().replace(" ", "-");
				String value = (String) method.invoke(instance);
				
				errors.add(
						ValidationError
							.builder()
							.code(StackTraceUtils.getCallerMethodName())
							.defaultMessage(message)
							.defaultMessageId(messageId)
							.field("fieldName")
							.objectName(objectName)
							.rejectedValue(value)
							.build()
				);
				
				
				System.out.println(errors.toString());
				
				return new ValidationExceptionBuilder();
				
			} catch (SecurityException | NoSuchMethodException | IllegalAccessException 
						| IllegalArgumentException | InvocationTargetException e) {	
				
				throw new RuntimeException(e);
			}

			
		}
		
		public static ValidationExceptionBuilder build() {
			return new ValidationExceptionBuilder();
		}
		
		public static void clear() {
			errors.clear();
		}
		
		public void throwsExcpeionIfHasErrors() {
			if (errors.isEmpty()) {
				return;
			}
			List<ValidationError> clone = new ArrayList<>();
			clone.addAll(errors);
			errors.clear();
			throw new ApiValidationFieldException(clone);
		}
		
	}