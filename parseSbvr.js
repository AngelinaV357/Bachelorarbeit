const fs = require('fs');
const SBVRParser = require('./sbvr-parser/sbvr-parser.js').SBVRParser.createInstance(); // Anpassen je nach Pfad

// Der Pfad zur SBVR-Datei
const sbvrFilePath = 'generated_rules.sbvr';

// Definition der Termini
const terms = {
    "Human Resources Department": "Human Resources Department",
    "IT-Department": "IT-Department",
    "Set up access rights, hardware and software": "Set up access rights, hardware and software",
    "Select necessary work equipment": "Select necessary work equipment",
    "Add personal data": "Add personal data",
    "Signed employment contract on file": "Signed employment contract on file",
    "Parallel Gateway": "Parallel Gateway",
    "Prepared for employee to start": "Prepared for employee to start",
    "the Process": "the Process",
    Start: "Start",
    End: "End"
};

// Definition der Verben
const verbs = {
    performs: "performs",
    starts: "starts with",
    ends: "ends with",
    follows: "follows"
};

// Definition der Fact-Typen
const factTypes = {
    Obligation: "Obligation",
    ProcessBehavior: "ProcessBehavior"
};

// Funktion zur Vorverarbeitung der Regeln
const preprocessRule = (rule) => {
    rule = rule
        .replace('It is obligatory that', factTypes.Obligation)
        .replace('after', verbs.follows)
        .replace('starts with', verbs.starts)
        .replace('ends with', verbs.ends)
        .replace('Human Resources Department', terms["Human Resources Department"])
        .replace('IT-Department', terms["IT-Department"])
        .replace('Set up access rights, hardware and software', terms["Set up access rights, hardware and software"])
        .replace('Select necessary work equipment', terms["Select necessary work equipment"])
        .replace('Add personal data', terms["Add personal data"])
        .replace('Signed employment contract on file', terms["Signed employment contract on file"])
        .replace('Parallel Gateway', terms["Parallel Gateway"])
        .replace('Prepared for employee to start', terms["Prepared for employee to start"])
        .trim();

    // Optional: Entferne überflüssige Anführungszeichen
    rule = rule.replace(/"([^"]*)"/g, '$1');
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
                const LF = SBVRParser.parse(preprocessedRule); // Nutze die richtige Parser-Methode
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
