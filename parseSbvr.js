const fs = require('fs');

// Importieren des SBVR-Parsers aus der lokalen Datei
const SBVRParser = require('./sbvr-parser/sbvr-parser.js').SBVRParser.createInstance();

// Dateiname mit den SBVR-Regeln
const inputFile = 'generated_rules.sbvr';

// Datei einlesen
try {
    const sbvrRules = fs.readFileSync(inputFile, 'utf8');

    // Jede Regel verarbeiten (Zeile für Zeile)
    const rules = sbvrRules.split('\n').filter(line => line.trim() !== '');

    rules.forEach((rule, index) => {
        try {
            const LF = SBVRParser.matchAll(rule, 'Process');
            console.log(`Rule ${index + 1}:`);
            console.log('Parsed Logical Form (LF):', LF);
        } catch (error) {
            console.log(`Parsing Rule ${index + 1}: ${rule}`);
        }
    });
} catch (fileError) {
    console.error('Error reading file:', fileError);
}
