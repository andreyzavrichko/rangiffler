package rangiffler.service;

import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.NoSuchElementException;

/**
 * Обработчик исключений для GraphQL, который разрешает ошибки, возникшие во время выполнения DataFetcher.
 * В зависимости от типа исключения, он возвращает соответствующий GraphQL error с указанием типа ошибки.
 */
@Slf4j
@Component
public class GraphqlExceptionHandler extends DataFetcherExceptionResolverAdapter {

  /**
   * Метод для обработки исключений, возникших при выполнении запросов GraphQL.
   * В зависимости от типа исключения будет возвращен соответствующий ответ с указанием типа ошибки.
   *
   * @param ex исключение, которое было выброшено
   * @param env контекст выполнения запроса GraphQL
   * @return объект GraphQLError, который будет отправлен в ответ
   */
  @Override
  protected GraphQLError resolveToSingleError(@Nonnull Throwable ex, @Nonnull DataFetchingEnvironment env) {
    // Логирование ошибки
    log.error(ex.getMessage(), ex);

    // Обработка исключений с типами IllegalArgumentException и IllegalStateException
    if (ex instanceof IllegalArgumentException || ex instanceof IllegalStateException) {
      return GraphqlErrorBuilder.newError()
              .errorType(ErrorType.BAD_REQUEST)
              .message(ex.getMessage())
              .path(env.getExecutionStepInfo().getPath())
              .location(env.getField().getSourceLocation())
              .build();
    }

    // Обработка исключений типа NoSuchElementException
    if (ex instanceof NoSuchElementException) {
      return GraphqlErrorBuilder.newError()
              .errorType(ErrorType.NOT_FOUND)
              .message(ex.getMessage())
              .path(env.getExecutionStepInfo().getPath())
              .location(env.getField().getSourceLocation())
              .build();
    }

    // Обработка других типов исключений как внутренних ошибок
    return GraphqlErrorBuilder.newError()
            .errorType(ErrorType.INTERNAL_ERROR)
            .message(ex.getMessage())
            .path(env.getExecutionStepInfo().getPath())
            .location(env.getField().getSourceLocation())
            .build();
  }
}
