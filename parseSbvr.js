const fs = require('fs');
const SBVRParser = require('./sbvr-parser/sbvr-parser.js').SBVRParser.createInstance(); // Anpassen je nach Pfad

// Der Pfad zur SBVR-Datei
const sbvrFilePath = 'generated_rules.sbvr';

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
        if (rule.trim()) {
            try {
                // Parsen der Regel
                const LF = SBVRParser.matchAll(rule, 'Process'); // Hier kannst du je nach Bedarf den richtigen Kontext wählen
                console.log(`Logische Form für Regel: ${rule}`);
                console.log(LF);
            } catch (error) {
                console.error(`Fehler beim Parsen der Regel: ${rule}`, error);
            }
        }
    });
});
