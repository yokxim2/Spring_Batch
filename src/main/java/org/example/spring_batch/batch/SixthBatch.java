package org.example.spring_batch.batch;

import org.example.spring_batch.entity.AfterEntity;
import org.example.spring_batch.entity.BeforeEntity;
import org.example.spring_batch.repository.BeforeRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemStreamWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;

import java.io.IOException;
import java.util.Map;

@Configuration
public class SixthBatch {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final BeforeRepository beforeRepository;

    public SixthBatch(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager, BeforeRepository beforeRepository) {
        this.jobRepository = jobRepository;
        this.platformTransactionManager = platformTransactionManager;
        this.beforeRepository = beforeRepository;
    }

    @Bean
    public Job sixthJob() {

        System.out.println("sixth job");

        return new JobBuilder("sixthJob", jobRepository)
                .start(sixthStep())
                .build();
    }

    @Bean
    public Step sixthStep() {

        System.out.println("sixth step");

        return new StepBuilder("sixthStep", jobRepository)
                .<BeforeEntity, BeforeEntity> chunk(10, platformTransactionManager)
                .reader(sixthBeforeReader())
                .processor(sixthProcessor())
                .writer(excelWriter())
                .build();
    }

    @Bean
    public RepositoryItemReader<BeforeEntity> sixthBeforeReader() {

        RepositoryItemReader<BeforeEntity> reader = new RepositoryItemReaderBuilder<BeforeEntity>()
                .name("beforeReader")
                .pageSize(10)
                .methodName("findAll")
                .repository(beforeRepository)
                .sorts(Map.of("id", Sort.Direction.ASC))
                .build();

        // 전체 데이터 셋에서 어디까지 수행 했는지의 값을 저장하지 않음
        reader.setSaveState(false);

        return reader;
    }

//    @Bean
//    public JdbcPagingItemReader<BeforeEntity> beforeSixthReader() {
//
//        return new JdbcPagingItemReaderBuilder<BeforeEntity>()
//                .name("beforeSixthReader")
//                .dataSource(dataSource)
//                .selectClause("SELECT id, username")
//                .fromClause("FROM BeforeEntity")
//                .sortKeys(Map.of("id", Order.ASCENDING))
//                .rowMapper(new CustomBeforeRowMapper())
//                .pageSize(10)
//                .build();
//    }

    @Bean
    public ItemProcessor<BeforeEntity, BeforeEntity> sixthProcessor() {

        return item -> item;
    }

    @Bean
    public ItemStreamWriter<BeforeEntity> excelWriter() {

        try {
            return new ExcelRowWriter("C:\\Users\\yooks\\OneDrive\\Desktop\\result.xlsx");
            //리눅스나 맥은 /User/형태로
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

//    @Bean
//    public JdbcBatchItemWriter<AfterEntity> afterSixthWriter() {
//
//        String sql = "INSERT INTO AfterEntity (username) VALUES (:username)";
//
//        return new JdbcBatchItemWriterBuilder<AfterEntity>()
//                .dataSource(dataSource)
//                .sql(sql)
//                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
//                .build();
//    }
}

