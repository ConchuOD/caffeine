package ie.ucd.engac.lifegamelogic.cards.occupationcards.collegecareercards;

import ie.ucd.engac.lifegamelogic.cards.occupationcards.OccupationCard;
import ie.ucd.engac.lifegamelogic.cards.occupationcards.OccupationCardTypes;
import ie.ucd.engac.messaging.Chooseable;

public class CollegeCareerCard extends OccupationCard implements Chooseable {
	private final CollegeCareerTypes collegeCareerType;
	
	public CollegeCareerCard(CollegeCareerTypes careerType,
							 int salary,
							 int bonusNumber,
							 int bonusPaymentAmount) {
		occupationCardType = OccupationCardTypes.CollegeCareer;
		this.collegeCareerType = careerType;
		this.salary = salary;
		this.bonusNumber = bonusNumber;
		this.bonusPaymentAmount = bonusPaymentAmount;
	}
	
	public CollegeCareerTypes getCareerType() {
		return collegeCareerType;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj == null) {
			return false;
		}
		
		if(!CollegeCareerCard.class.isAssignableFrom(obj.getClass())) {
			return false;
		}
		
		final CollegeCareerCard otherCCCard = (CollegeCareerCard) obj;
		
		if(this.collegeCareerType != otherCCCard.getCareerType()) {
			return false;
		}
		
		if(this.getOccupationCardType() != otherCCCard.getOccupationCardType()) {
			return false;
		}
		
		if(this.getBonusPaymentAmount() != otherCCCard.getBonusPaymentAmount()) {
			return false;
		}
		
		if(this.getBonusNumber() != otherCCCard.getBonusNumber()) {
			return false;
		}
		
		if(this.getSalary() != otherCCCard.getSalary()) {
			return false;
		}
		
		return true;
	}
	
	@Override
	public String displayChoiceDetails() {
		String string = "";
		string = string.concat(occupationCardType + ":\n ");
		string = string.concat(collegeCareerType.toString() + ",\n");
		string = string.concat(" Salary: " + salary + ",\n");
		string = string.concat(" Bonus payout: " + bonusPaymentAmount + ",\n");
		string = string.concat(" Bonus number: " + bonusNumber + "\n");
		return string;
	}
}
