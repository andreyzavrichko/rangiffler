package rangiffler.jupiter.annotation;



import rangiffler.model.testdata.CountryCodes;
import rangiffler.model.testdata.PhotoFiles;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface WithPhoto {

  CountryCodes countryCode() default CountryCodes.US;

  PhotoFiles image() default PhotoFiles.AMSTERDAM;

  String description() default "";

  int likes() default 0;
}
