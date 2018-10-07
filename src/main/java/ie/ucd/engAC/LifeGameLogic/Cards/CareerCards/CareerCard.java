package ie.ucd.engAC.LifeGameLogic.Cards.CareerCards;

import ie.ucd.engAC.LifeGameLogic.Cards.Card;

public class CareerCard extends Card {
	private final CareerTypes careerType;
	
	public CareerTypes getCareerType() {
		return careerType;
	}
	
	private final int salary;
	
	public int getSalary() {
		return salary;
	}
	
	private final int bonusNumber;
	
	public int getBonusNumber() {
		return bonusNumber;
	}
	
	private final int bonusPaymentAmount;
	
	public int getBonusPaymentAmount() {
		return bonusPaymentAmount;
	}
	
	public CareerCard(CareerTypes careerType,
					  int salary,
					  int bonusNumber,
					  int bonusPaymentAmount) {
		this.careerType = careerType;
		this.salary = salary;
		this.bonusNumber = bonusNumber;
		this.bonusPaymentAmount = bonusPaymentAmount;
	}
}
