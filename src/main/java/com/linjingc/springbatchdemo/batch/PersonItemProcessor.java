package com.linjingc.springbatchdemo.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

/**
 * ItemProcessor 转换器
 * Person ->转换为 PersonNext
 */
public class PersonItemProcessor implements ItemProcessor<Person, PersonNext> {

    private static final Logger log = LoggerFactory.getLogger(PersonItemProcessor.class);

    @Override
    public PersonNext process(final Person person) {
        final String firstName = person.getFirstName().toUpperCase();
        final String lastName = person.getLastName().toUpperCase();

        final PersonNext transformedPerson = new PersonNext(firstName, lastName);

        log.info("Converting (" + person + ") into (" + transformedPerson + ")");

        return transformedPerson;
    }

}