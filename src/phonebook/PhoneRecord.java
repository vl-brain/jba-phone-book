package phonebook;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

public class PhoneRecord
{
	private final String number;
	private final Person person;

	public PhoneRecord(String number, Person person)
	{
		this.number = requireNonNull(number, "Number required!");
		this.person = requireNonNull(person, "Person required!");
	}

	public static PhoneRecord parse(String value) {
		final String[] parts = value.split("\\s+", 2);
		if (parts.length < 2) {
			throw new IllegalArgumentException("Can't parse PhoneRecord from: " + value);
		}
		return new PhoneRecord(parts[0], Person.parse(parts[1]));
	}

	public String getNumber()
	{
		return number;
	}

	public Person getPerson()
	{
		return person;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (o == null || getClass() != o.getClass())
		{
			return false;
		}
		PhoneRecord record = (PhoneRecord) o;
		return number.equals(record.number) &&
			person.equals(record.person);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(number, person);
	}
}
