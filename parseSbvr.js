const fs = require('fs');
const SBVRParser = require('./sbvr-parser/sbvr-parser.js').SBVRParser.createInstance(); // Anpassen je nach Pfad

// Der Pfad zur SBVR-Datei
const sbvrFilePath = 'generated_rules.sbvr';

// Funktion zur Vorverarbeitung der Regeln
const preprocessRule = (rule) => {
    // Beispiel für einfache Transformationen
    return rule
        .replace('It is permitted that', 'Permission:')
        .replace('receives a message from', 'receives:');
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
                const LF = SBVRParser.matchAll(preprocessedRule, 'Process');
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

