package com.atmire.cocoon.matching;

import static java.util.regex.Pattern.compile;
import static org.apache.cocoon.environment.ObjectModelHelper.getRequest;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.matching.Matcher;

public class RegexpRequestParameterNameMatcher implements Matcher {

    public Map match(String pattern, Map objectModel, Parameters parameters) {

        Enumeration parameterNames = getRequest(objectModel).getParameterNames();
        while (parameterNames.hasMoreElements()) {
            Object element = parameterNames.nextElement();
            final java.util.regex.Matcher matcher = compile(pattern).matcher((String) element);
            if (matcher.matches()) {
                return new HashMap() {
                    {
                        for (int i = 0; i <= matcher.groupCount(); i++) {
                            put(i + "", matcher.group(1));
                        }
                    }
                };
            }
        }

        return null;
    }
}
