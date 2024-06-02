const express = require('express');
const bodyParser = require('body-parser');
const { google } = require('googleapis');
const fs = require('fs');

const app = express();
app.use(bodyParser.json());

// Configurar autenticación de Google Sheets
const SCOPES = ['https://www.googleapis.com/auth/spreadsheets'];
const credentials = JSON.parse(fs.readFileSync('./collar-geolocalizable-c7c7d7a1eeab.json')); // Asegúrate de que el archivo JSON esté en el directorio correcto

const auth = new google.auth.GoogleAuth({
    credentials,
    scopes: SCOPES,
});

const sheets = google.sheets({ version: 'v4', auth });

const spreadsheetId = '1GgW3UBmAIL9mLxBZt0-t4iZe5_yjx9tDhxH-yshAuRM'; // ID de la hoja de cálculo

app.post('/guardar-ubicacion', async (req, res) => {
    const { latitude, longitude } = req.body;

    const values = [
        [new Date().toISOString(), latitude, longitude],
    ];

    const resource = {
        values,
    };

    try {
        await sheets.spreadsheets.values.append({
            spreadsheetId,
            range: 'Hoja 1!A:C', // Rango en el que se escribirán los datos
            valueInputOption: 'RAW',
            resource,
        });

        res.sendStatus(200);
    } catch (error) {
        console.error('Error escribiendo en Google Sheets:', error);
        res.sendStatus(500);
    }
});

app.listen(3000, () => {
    console.log('Servidor escuchando en el puerto 3000');
});
