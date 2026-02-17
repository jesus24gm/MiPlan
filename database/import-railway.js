// Script para importar base de datos a Railway
// Uso: node import-railway.js

const mysql = require('mysql2/promise');
const fs = require('fs');
const path = require('path');

async function importDatabase() {
  console.log('========================================');
  console.log('Importando Base de Datos a Railway');
  console.log('========================================\n');

  const connection = await mysql.createConnection({
    host: 'shinkansen.proxy.rlwy.net',
    port: 21599,
    user: 'root',
    password: 'QMhzJGtIKOKXvqsTtmYirRtajKxUPyBF',
    database: 'railway',
    multipleStatements: true
  });

  console.log('✅ Conectado a MySQL de Railway\n');

  const sqlPath = path.join(__dirname, 'schema.sql');
  const sql = fs.readFileSync(sqlPath, 'utf8');

  console.log('📄 Ejecutando schema.sql...\n');

  try {
    await connection.query(sql);
    console.log('========================================');
    console.log('✅ Base de datos importada exitosamente!');
    console.log('========================================\n');
    console.log('Tablas creadas:');
    console.log('  - users');
    console.log('  - tasks');
    console.log('  - boards');
    console.log('  - notifications');
    console.log('  - board_members\n');
  } catch (error) {
    console.error('❌ Error al importar:', error.message);
    process.exit(1);
  } finally {
    await connection.end();
  }
}

importDatabase();
