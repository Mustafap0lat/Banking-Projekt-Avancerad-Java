package se.sensera.banking.Implements;

import lombok.AllArgsConstructor;
import lombok.Data;
import se.sensera.banking.User;

@Data
@AllArgsConstructor
public class UserImpl implements User {

String id;
String name;
String personalIdentificationNumber;
boolean active;

}
