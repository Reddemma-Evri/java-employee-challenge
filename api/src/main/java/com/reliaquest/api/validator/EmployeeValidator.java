package com.reliaquest.api.validator;

import com.reliaquest.api.constant.ValidatorConstants;
import com.reliaquest.api.dto.EmployeeRequestDTO;
import com.reliaquest.api.exception.EmployeeException;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class EmployeeValidator {

    private final MessageSource messageSource;

    @Autowired
    public EmployeeValidator(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public void validateUUID(String id) {
        isEmpty(id, "employee.id.notBlank", LocaleContextHolder.getLocale(), messageSource);
        if (!id.matches(ValidatorConstants.UUID_VALIDATOR)) {
            throw new EmployeeException(
                    messageSource.getMessage("employee.id.invalidUUID", null, LocaleContextHolder.getLocale()));
        }
    }

    public void validateEmployeeName(String empName) {
        isEmpty(empName, "employee.name.notBlank", LocaleContextHolder.getLocale(), messageSource);
    }

    public void validateEmployeeRequest(EmployeeRequestDTO input) {
        validateEmployeeName(input.name());
        validateSalary(input.salary());
        validateAge(input.age());
        validateTitle(input.title());
    }

    private static void isEmpty(Object value, String msgProp, Locale locale, MessageSource messageSource) {
        boolean isEmpty = (value == null)
                || (value instanceof String str && !StringUtils.hasText(str))
                || (value instanceof Number num && num.doubleValue() == 0.0)
                || (value instanceof Boolean bool && !bool);
        if (isEmpty) {
            String message = messageSource.getMessage(msgProp, null, locale);
            throw new EmployeeException(message);
        }
    }

    public void validateSalary(Integer salary) {
        isEmpty(salary, "employee.salary.notBlank", LocaleContextHolder.getLocale(), messageSource);
        if (salary == null || salary <= 0) {
            throw new EmployeeException(
                    messageSource.getMessage("employee.salary.positive", null, LocaleContextHolder.getLocale()));
        }
    }

    public void validateAge(Integer age) {
        isEmpty(age, "employee.age.notBlank", LocaleContextHolder.getLocale(), messageSource);
        if (age == null || age < 16 || age > 75) {
            throw new EmployeeException(
                    messageSource.getMessage("employee.age.range", null, LocaleContextHolder.getLocale()));
        }
    }

    public void validateTitle(String title) {
        isEmpty(title, "employee.title.notBlank", LocaleContextHolder.getLocale(), messageSource);
        if (!StringUtils.hasText(title)) {
            throw new EmployeeException(
                    messageSource.getMessage("employee.title.notBlank", null, LocaleContextHolder.getLocale()));
        }
    }
}
