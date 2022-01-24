package se.sensera.banking.Implements;

import lombok.AllArgsConstructor;
import se.sensera.banking.User;
import se.sensera.banking.UserService;
import se.sensera.banking.UsersRepository;
import se.sensera.banking.exceptions.Activity;
import se.sensera.banking.exceptions.UseException;
import se.sensera.banking.exceptions.UseExceptionType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Stream;

@AllArgsConstructor

public class UserServiceImpl implements UserService {
    private UsersRepository usersRepository;
    private User user;

    public UserServiceImpl(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }


    @Override
    public User createUser(String name, String personalIdentificationNumber) throws UseException {
        if (usersRepository.all()
                .anyMatch(user1 -> user1.getPersonalIdentificationNumber().equals(personalIdentificationNumber)))

            throw new UseException(Activity.CREATE_USER, UseExceptionType.USER_PERSONAL_ID_NOT_UNIQUE);

        UserImpl user = new UserImpl(UUID.randomUUID().toString(), name, personalIdentificationNumber, true);
        usersRepository.save(user);
        return user;
    }

    @Override
    public User changeUser(String userId, Consumer<ChangeUser> changeUser) throws UseException {
        if (usersRepository.getEntityById(userId).isPresent()) {

            changeUser.accept(new ChangeUser() {

                @Override
                public void setName(String name) {
                    setName(name);
                    // overridar setName till att ta från klassen User
                }

                @Override
                public void setPersonalIdentificationNumber(String personalIdentificationNumber) throws UseException {
//                    if(!usersRepository.all().noneMatch(user1 -> user1.getPersonalIdentificationNumber().equals(personalIdentificationNumber))){

                    user.setPersonalIdentificationNumber(personalIdentificationNumber);
//                    }else {
//                     throw   new UseException(Activity.UPDATE_USER, UseExceptionType.USER_PERSONAL_ID_NOT_UNIQUE);
//                    }
                }
            });
            return usersRepository.save(user);
        }else{
            throw new UseException(Activity.UPDATE_USER, UseExceptionType.NOT_FOUND);

        }

    }

    @Override
    public User inactivateUser(String userId) throws UseException {
        return null;
    }

    @Override
    public Optional<User> getUser(String userId) {

        if (!user.getId().equals(userId)) {
            return Optional.empty();
        }
        return usersRepository.getEntityById(userId);
    }

    @Override
    public Stream<User> find(String searchString, Integer pageNumber, Integer pageSize, SortOrder sortOrder) {
        return null;
    }
}
