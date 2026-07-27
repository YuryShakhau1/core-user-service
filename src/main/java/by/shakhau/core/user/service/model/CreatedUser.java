package by.shakhau.core.user.service.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(of = { }, callSuper = true)
public class CreatedUser extends User {

    private StringBuilder tempPassword;
}
