package nz.ac.auckland.cer.jobaudit.util;

import java.util.Comparator;

import nz.ac.auckland.cer.jobaudit.pojo.User;

public class UserComparator implements Comparator<User> {

	public int compare(User u1, User u2) {
		if (u1 == null || u2 == null) {
			throw new RuntimeException("Can't compare null objects");
		}
		if (u1.getName() == null || u2.getName() == null) {
			throw new RuntimeException("Can't compare User objects with name null");
		}
		return u1.getName().compareTo(u2.getName());
	}

}
