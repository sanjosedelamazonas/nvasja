package org.sanjose.converter;


import com.vaadin.data.Converter;
import com.vaadin.data.Result;
//import com.vaadin.data.SimpleResult;
import com.vaadin.data.ValueContext;
import com.vaadin.server.SerializableConsumer;
import com.vaadin.server.SerializableFunction;

import java.util.Locale;
import java.util.Optional;

public class ZeroOneToBooleanConverter implements Converter<Boolean, Character> {

    @Override
    public Result<Character> convertToModel(Boolean b, ValueContext valueContext) {
        //if (b != null && b)
            return new Result<Character>() {
            @Override
            public <S> Result<S> flatMap(SerializableFunction<Character, Result<S>> serializableFunction) {
                return null;
            }

            @Override
            public void handle(SerializableConsumer<Character> serializableConsumer, SerializableConsumer<String> serializableConsumer1) {

            }

            @Override
            public boolean isError() {
                return false;
            }

            @Override
            public Optional<String> getMessage() {
                return Optional.empty();
            }

            @Override
            public <X extends Throwable> Character getOrThrow(SerializableFunction<String, ? extends X> serializableFunction) throws X {
                return null;
            }
        };
        //else return new SimpleResult<Boolean>('0',"Must");;
    }

    @Override
    public Boolean convertToPresentation(Character character, ValueContext valueContext) {
        if (valueContext.getHasValue().get() == null || valueContext.getHasValue().get().equals('0')) {
            return false;
        } else {
            return true;
        }
    }
}