const sharp = require('sharp');
const path = require('path');
const fs = require('fs');

const svgPath = path.join(__dirname, '../assets/branding/logo.svg');
const outPath = path.join(__dirname, '../assets/branding/play_store_512.png');

console.log('Generating Play Store Icon from SVG...');

// Google Play Store requires a 512x512 32-bit PNG.
// We will composite the transparent SVG on top of a #1A1A2E background.
sharp({
  create: {
    width: 512,
    height: 512,
    channels: 4,
    background: { r: 26, g: 26, b: 46, alpha: 1 } // #1A1A2E
  }
})
  .composite([
    {
      input: svgPath,
      blend: 'over'
    }
  ])
  .png()
  .toFile(outPath)
  .then((info) => {
    console.log('Success! PNG generated at:', outPath);
    console.log(info);
  })
  .catch((err) => {
    console.error('Error generating PNG:', err);
    process.exit(1);
  });
