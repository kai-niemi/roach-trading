package io.roach.trading.domain.common;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Simple zoom expression representing a projection rule for link relations.
 */
public class ZoomExpression implements Iterable<String> {
    private final String expression;

    private final List<String> relations = new LinkedList<>();

    private ZoomExpression(String expression) {
        this.expression = expression;
    }

    public static ZoomExpression fromCurrentRequest() {
        HttpServletRequest currentRequest =
                ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                        .getRequest();
        String zoom = ServletRequestUtils.getStringParameter(currentRequest, "zoom", "");
        return ZoomExpression.from(zoom);
    }

    public static ZoomExpression from(String expression) {
        ZoomExpression ze = new ZoomExpression(expression);
        String parts[] = expression.split(("\\s*,\\s*"));
        for (String part : parts) {
            ze.add(part);
        }
        return ze;
    }

    public void add(String rel) {
        this.relations.add(rel);
    }

    @Override
    public Iterator<String> iterator() {
        return Collections.unmodifiableList(relations).iterator();
    }

    public String getExpression() {
        return expression;
    }

    public boolean containsRel(String name) {
        for (String relation : relations) {
            if (name.equals(relation)) {
                return true;
            }
        }
        return false;
    }

    public String rel(String name) {
        for (String relation : relations) {
            if (name.equals(relation)) {
                return relation;
            }
        }
        return null;
    }
}
