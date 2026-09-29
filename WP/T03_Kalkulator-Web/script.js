function kalkulator(a, b, operator) {
    
	if (operator === "/" && b === 0) {
		return "Error: Pembagian dengan 0 tidak diperbolehkan!";
	}

	if (operator === "+") return a + b;
	else if (operator === "-") return a - b;
	else if (operator === "*") return a * b;
	else if (operator === "/") return a / b;
	else return "Error: Operator tidak valid";
}

const calculatorForm = document.querySelector("#calculator-form");
const result = document.querySelector("#result");
const firstNumberInput = document.querySelector("#first-number");
const secondNumberInput = document.querySelector("#second-number");

calculatorForm.addEventListener("submit", (event) => {
	event.preventDefault();

	if (firstNumberInput.value === "" || secondNumberInput.value === "") {
		result.textContent = "...";
		result.classList.remove("has-result");
		return;
	}

	result.textContent = String(kalkulator(firstNumberInput.valueAsNumber, secondNumberInput.valueAsNumber, document.querySelector("#operator").value));
	result.classList.add("has-result");
});
