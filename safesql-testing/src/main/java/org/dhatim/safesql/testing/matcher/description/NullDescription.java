package org.dhatim.safesql.testing.matcher.description;

class NullDescription implements Description {

    @Override
    public Description appendDescriptionOf(SelfDescribing value) {
        return this;
    }

    @Override
    public Description appendList(String start, String separator, String end, Iterable<? extends SelfDescribing> values) {
        return this;
    }

    @Override
    public Description appendText(String text) {
        return this;
    }

    @Override
    public Description appendValue(Object value) {
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Description appendValueList(String start, String separator, String end, T... values) {
        return this;
    }

    @Override
    public <T> Description appendValueList(String start, String separator, String end, Iterable<T> values) {
        return this;
    }

    @Override
    public String toString() {
        return "";
    }
    
}
