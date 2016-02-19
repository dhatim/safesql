package org.dhatim.safesql.builder;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QueryBuilderContext implements AliasOwner {
    
    private static final Pattern PATTERN = Pattern.compile("(.*_)([0-9]+)$");

    private final Set<Alias> generatedAliases = new HashSet<>();
    private final Set<String> generatedIdentifiers = new HashSet<>();
    
    public QueryBuilderContext() {
    }
    
    QueryBuilderContext(QueryBuilderContext other) {
        this(other.generatedAliases, other.generatedIdentifiers);
    }
    
    private QueryBuilderContext(Set<Alias> generatedAliases, Set<String> generatedIdentifiers) {
        this.generatedAliases.addAll(generatedAliases);
        this.generatedIdentifiers.addAll(generatedIdentifiers);
    }
    
    public Alias generate(String suggestion) {
        Alias suggestedAlias = new Alias(this, suggestion);
        while (generatedAliases.contains(suggestedAlias)) {
            Matcher matcher = PATTERN.matcher(suggestedAlias.getName());
            if (matcher.matches()) {
                int n = Integer.parseInt(matcher.group(2));
                suggestedAlias = new Alias(this, matcher.group(1) + (n + 1));
            } else {
                suggestedAlias = new Alias(this, suggestedAlias.getName() + "_1");
            }
        }
        generatedAliases.add(suggestedAlias);
        return suggestedAlias;
    }
    
    public String generateIdentifier(String suggestion) {
        String identifier = suggestion;
        while (generatedIdentifiers.contains(identifier)) {
            Matcher matcher = PATTERN.matcher(identifier);
            if (matcher.matches()) {
                int n = Integer.parseInt(matcher.group(2));
                identifier = matcher.group(1) + (n + 1);
            } else {
                identifier = suggestion + "_1";
            }
        }
        generatedIdentifiers.add(identifier);
        return identifier;
    }
    
}
