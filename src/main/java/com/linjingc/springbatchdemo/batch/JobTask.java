package com.linjingc.springbatchdemo.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.sql.DataSource;


@Configuration
@EnableBatchProcessing
//@Import(DataSourceConfiguration.class)
public class JobTask {
    @Autowired
    private JobBuilderFactory jobs;
    @Autowired
    private StepBuilderFactory steps;

    @Bean
    public Job importUserJob(JobCompletionNotificationListener jobCompletionNotificationListener, @Qualifier("step1") Step step1) {
        return jobs.get("importUserJob")
                .incrementer(new RunIdIncrementer())
                .listener(jobCompletionNotificationListener)
                .flow(step1)
                .end()
                .build();
    }


    @Bean
    protected Step step1(ItemReader<Person> reader,
                         PersonItemProcessor processor,
                         ItemWriter<PersonNext> writer) {
        return steps.get("step1")
                //这里有一个chunk的设置，值10，意思是10条记录后再提交输出
                .<Person, PersonNext>chunk(10)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }


    /**
     * 输入 读取文本内容.CSV
     */
    @Bean
    public FlatFileItemReader<Person> reader() {
        return new FlatFileItemReaderBuilder<Person>()
                .name("personItemReader")
                .resource(new ClassPathResource("sample-data.csv"))
                //跳过CSV第一行,表头
                .linesToSkip(1)
                .delimited()
                //字段名
                .names(new String[]{"id,", "firstName", "lastName"})
                .fieldSetMapper(new BeanWrapperFieldSetMapper<Person>() {{
                    //转换后的目标类
                    setTargetType(Person.class);
                }})
                .build();
    }


    /**
     * 处理器
     *
     * @return
     */
    @Bean
    public PersonItemProcessor processor() {
        return new PersonItemProcessor();
    }


    /**
     * 输出
     *
     * @param dataSource
     * @return
     */
    @Bean
    public JdbcBatchItemWriter<PersonNext> writer(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<PersonNext>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("INSERT INTO people (first_name, last_name) VALUES (:firstName, :lastName)")
                .dataSource(dataSource)
                .build();
    }
}
