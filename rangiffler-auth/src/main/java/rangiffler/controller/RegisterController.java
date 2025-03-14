package rangiffler.controller;

import jakarta.annotation.Nonnull;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import rangiffler.model.RegistrationModel;
import rangiffler.service.UserService;

/**
 * Контроллер для обработки регистрации пользователя.
 * <p>
 * Обрабатывает отображение страницы регистрации и регистрацию пользователя в системе.
 */
@Controller
public class RegisterController {

    private static final Logger LOG = LoggerFactory.getLogger(RegisterController.class);

    // Константы для модели и атрибутов
    private static final String REGISTRATION_VIEW_NAME = "register";
    private static final String MODEL_USERNAME_ATTR = "username";
    private static final String MODEL_REG_FORM_ATTR = "registrationModel";
    private static final String MODEL_FRONT_URI_ATTR = "frontUri";
    private static final String REG_MODEL_ERROR_BEAN_NAME = "org.springframework.validation.BindingResult.registrationModel";

    private final UserService userService;
    private final String rangifflerFrontUri;

    /**
     * Конструктор контроллера регистрации.
     *
     * @param userService        сервис для регистрации пользователя.
     * @param rangifflerFrontUri URI для перенаправлений на фронтенд.
     */
    @Autowired
    public RegisterController(UserService userService,
                              @Value("${rangiffler-front.base-uri}") String rangifflerFrontUri) {
        this.userService = userService;
        this.rangifflerFrontUri = rangifflerFrontUri;
    }

    /**
     * Обрабатывает GET-запросы на страницу регистрации.
     *
     * @param model модель для передачи данных в представление.
     * @return имя представления для страницы регистрации.
     */
    @GetMapping("/register")
    public String getRegisterPage(@Nonnull Model model) {
        model.addAttribute(MODEL_REG_FORM_ATTR, new RegistrationModel(null, null, null));
        model.addAttribute(MODEL_FRONT_URI_ATTR, rangifflerFrontUri + "/redirect");
        return REGISTRATION_VIEW_NAME;
    }

    /**
     * Обрабатывает POST-запросы для регистрации нового пользователя.
     *
     * @param registrationModel модель данных формы регистрации.
     * @param errors            ошибки валидации формы.
     * @param model             модель для передачи данных в представление.
     * @param request           HTTP запрос.
     * @param response          HTTP ответ.
     * @return имя представления для страницы регистрации.
     */
    @PostMapping(value = "/register")
    public String registerUser(@Valid @ModelAttribute RegistrationModel registrationModel,
                               Errors errors,
                               Model model,
                               HttpServletRequest request,
                               HttpServletResponse response) {
        if (!errors.hasErrors()) {
            final String registeredUserName;

            try {
                registeredUserName = userService.registerUser(
                        registrationModel.username(),
                        registrationModel.password(),
                        "ru"
                );
                response.setStatus(HttpServletResponse.SC_CREATED);
                model.addAttribute(MODEL_USERNAME_ATTR, registeredUserName);
            } catch (DataIntegrityViolationException e) {
                LOG.error("### Error while registration user: {}", e.getMessage());
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                addErrorToRegistrationModel(
                        registrationModel,
                        model,
                        "username", "Username `" + registrationModel.username() + "` already exists"
                );
            }
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
        model.addAttribute(MODEL_FRONT_URI_ATTR, rangifflerFrontUri + "/redirect");
        return REGISTRATION_VIEW_NAME;
    }

    /**
     * Добавляет ошибку в модель регистрации.
     *
     * @param registrationModel модель регистрации.
     * @param model             модель для передачи данных в представление.
     * @param fieldName         имя поля, в котором возникла ошибка.
     * @param error             описание ошибки.
     */
    private void addErrorToRegistrationModel(@Nonnull RegistrationModel registrationModel,
                                             @Nonnull Model model,
                                             @Nonnull String fieldName,
                                             @Nonnull String error) {
        // Получаем результат ошибок для формы
        BeanPropertyBindingResult errorResult = (BeanPropertyBindingResult) model.getAttribute(REG_MODEL_ERROR_BEAN_NAME);
        if (errorResult == null) {
            errorResult = new BeanPropertyBindingResult(registrationModel, "registrationModel");
        }
        // Добавляем ошибку
        errorResult.addError(new FieldError("registrationModel", fieldName, error));
    }
}
