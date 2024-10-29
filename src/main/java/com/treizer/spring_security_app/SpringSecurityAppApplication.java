package com.treizer.spring_security_app;

import java.util.List;
import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.treizer.spring_security_app.persistence.entities.PermissionEntity;
import com.treizer.spring_security_app.persistence.entities.RoleEntity;
import com.treizer.spring_security_app.persistence.entities.RoleEnum;
import com.treizer.spring_security_app.persistence.entities.UserEntity;
import com.treizer.spring_security_app.persistence.repositories.IUserRepository;

@SpringBootApplication
public class SpringSecurityAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringSecurityAppApplication.class, args);
	}

	@Bean
	CommandLineRunner init(IUserRepository userRepository) {
		return args -> {
			/* CREATE PERMISSIONS */
			PermissionEntity createPermission = PermissionEntity.builder()
					.name("CREATE")
					.build();

			PermissionEntity readPermission = PermissionEntity.builder()
					.name("READ")
					.build();

			PermissionEntity updatePermission = PermissionEntity.builder()
					.name("UPDATE")
					.build();

			PermissionEntity deletePermission = PermissionEntity.builder()
					.name("DELETE")
					.build();

			PermissionEntity refactorPermission = PermissionEntity.builder()
					.name("REFACTOR")
					.build();

			/* Create ROLES */
			RoleEntity roleAdmin = RoleEntity.builder()
					.roleName(RoleEnum.ADMIN)
					.permissions(Set.of(createPermission, readPermission, updatePermission, deletePermission))
					.build();

			RoleEntity roleUser = RoleEntity.builder()
					.roleName(RoleEnum.USER)
					.permissions(Set.of(createPermission, readPermission))
					.build();

			RoleEntity roleInvited = RoleEntity.builder()
					.roleName(RoleEnum.INVITED)
					.permissions(Set.of(readPermission))
					.build();

			RoleEntity roleDeveloper = RoleEntity.builder()
					.roleName(RoleEnum.DEVELOPER)
					.permissions(Set.of(createPermission, readPermission, updatePermission, deletePermission,
							refactorPermission))
					.build();

			/* Create USERS */
			UserEntity userHugo = UserEntity.builder()
					.username("hugo")
					// .password("1234")
					.password("$2a$10$vVgxw0q/lOhcEYKQrr.VbOtBkIU8j8HJNwFpzkhJf5NH3EgGd9QIe")
					.isEnabled(true)
					.accountNoExpired(true)
					.accountNoLocked(true)
					.credentialNoExpired(true)
					.roles(Set.of(roleAdmin))
					.build();

			UserEntity userPaco = UserEntity.builder()
					.username("paco")
					// .password("1234")
					.password("$2a$10$vVgxw0q/lOhcEYKQrr.VbOtBkIU8j8HJNwFpzkhJf5NH3EgGd9QIe")
					.isEnabled(true)
					.accountNoExpired(true)
					.accountNoLocked(true)
					.credentialNoExpired(true)
					.roles(Set.of(roleUser))
					.build();

			UserEntity userLuis = UserEntity.builder()
					.username("luis")
					// .password("1234")
					.password("$2a$10$vVgxw0q/lOhcEYKQrr.VbOtBkIU8j8HJNwFpzkhJf5NH3EgGd9QIe")
					.isEnabled(true)
					.accountNoExpired(true)
					.accountNoLocked(true)
					.credentialNoExpired(true)
					.roles(Set.of(roleInvited))
					.build();

			UserEntity userAbril = UserEntity.builder()
					.username("abril")
					// .password("1234")
					.password("$2a$10$vVgxw0q/lOhcEYKQrr.VbOtBkIU8j8HJNwFpzkhJf5NH3EgGd9QIe")
					.isEnabled(true)
					.accountNoExpired(true)
					.accountNoLocked(true)
					.credentialNoExpired(true)
					.roles(Set.of(roleDeveloper))
					.build();

			userRepository.saveAll(List.of(userHugo, userPaco, userLuis, userAbril));
		};
	}

}
