package rangiffler.config.keys;

import com.nimbusds.jose.jwk.RSAKey;
import jakarta.annotation.Nonnull;
import org.springframework.stereotype.Component;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;

/**
 * Класс для управления генерацией RSA ключей.
 * Используется для создания пары публичного и приватного ключей RSA.
 */
@Component
public class KeyManager {

  /**
   * Генерирует пару RSA ключей (публичный и приватный) и возвращает их как {@link RSAKey}.
   *
   * @return объект {@link RSAKey}, содержащий публичный и приватный ключи.
   * @throws NoSuchAlgorithmException если алгоритм RSA недоступен в системе.
   */
  public @Nonnull RSAKey rsaKey() throws NoSuchAlgorithmException {
    try {
      // Инициализация генератора пары ключей RSA
      KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
      generator.initialize(2048); // Длина ключа 2048 бит
      KeyPair keyPair = generator.generateKeyPair();

      // Извлечение публичного и приватного ключей
      RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
      RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

      // Создание и возвращение объекта RSAKey с уникальным ID
      return new RSAKey.Builder(publicKey)
              .privateKey(privateKey)
              .keyID(UUID.randomUUID().toString())
              .build();
    } catch (NoSuchAlgorithmException e) {
      // Можно добавить логирование или обработку ошибки, если нужно
      throw new NoSuchAlgorithmException("Алгоритм RSA недоступен: " + e.getMessage(), e);
    }
  }
}
