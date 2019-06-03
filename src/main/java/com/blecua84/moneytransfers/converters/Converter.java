package com.blecua84.moneytransfers.converters;

import com.blecua84.moneytransfers.converters.exceptions.ConverterException;

public interface Converter<Source, Target> {

    Target convert(Source source) throws ConverterException;
}
