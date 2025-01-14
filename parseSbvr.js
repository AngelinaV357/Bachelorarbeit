const fs = require('fs');
const SBVRParser = require('./sbvr-parser/sbvr-parser.js').SBVRParser.createInstance(); // Anpassen je nach Pfad

// Der Pfad zur SBVR-Datei
const sbvrFilePath = 'generated_rules.sbvr';

// Definition der Termini
const terms = {
    HumanResourcesDept: "Human Resources Department", // Akteur
    SelectWorkEquipment: "Select necessary work equipment", // Aktivität 1
    AddPersonalData: "Add personal data", // Aktivität 2
    theProcess: "the Process", // Der Prozess (optional)
    Start: "Start", // Start-Aktion
    End: "End" // End-Aktion
};

// Definition der Verben
const verbs = {
    performs: "performs", // Standardverb für Handlungen
    starts: "starts with", // Starten eines Prozesses
    ends: "ends with", // Beenden eines Prozesses
    follows: "follows", // Nachfolgeraktion
};

// Definition der Fact-Typen
const factTypes = {
    Obligation: "Obligation", // Die Verpflichtung (z. B. in "It is obligatory that")
    ProcessBehavior: "ProcessBehavior", // Prozessverhalten für die Prozessregeln
};

// Funktion zur Vorverarbeitung der Regeln
const preprocessRule = (rule) => {
    rule = rule
        .replace('It is obligatory that', factTypes.Obligation) // Obligation für Regeln
        .replace(/Human Resources Department/g, terms.HumanResourcesDept) // Akteur ersetzen
        .replace('after', verbs.follows) // "after" durch "follows" ersetzen
        .replace('starts with', verbs.starts) // "starts with" durch "starts with" ersetzen
        .replace('ends with', verbs.ends) // "ends with" durch "ends with" ersetzen
        .replace('Select necessary work equipment', terms.SelectWorkEquipment) // Aktivität 1
        .replace('Add personal data', terms.AddPersonalData) // Aktivität 2
        .trim();

    return rule;
};

// Lies die SBVR-Datei ein
fs.readFile(sbvrFilePath, 'utf8', (err, data) => {
    if (err) {
        console.error('Fehler beim Lesen der Datei:', err);
        return;
    }

    // Zeige den Inhalt der SBVR-Datei
    console.log('SBVR-Datei-Inhalt:\n', data);

    // Jede Regel aus der Datei verarbeiten
    const rules = data.split('\n'); // Annahme: jede Regel ist durch eine neue Zeile getrennt

    rules.forEach(rule => {
        if (rule.trim() && rule.length < 200) { // Beschränkung auf kürzere Regeln
            try {
                const preprocessedRule = preprocessRule(rule);
                console.log(`Vorverarbeitete Regel: ${preprocessedRule}`);

                // Versuche, die vorverarbeitete Regel zu parsen
                const LF = SBVRParser.matchAll(preprocessedRule, factTypes.Obligation);
                console.log(`Logische Form für Regel: ${preprocessedRule}`);
                console.log(LF);
            } catch (error) {
                console.error(`Fehler beim Parsen der Regel: ${rule}`, error.message);
            }
        } else {
            console.warn(`Überspringe komplexe Regel: ${rule}`);
        }
    });
});
