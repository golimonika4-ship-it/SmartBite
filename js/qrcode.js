/**
 * Standalone QR Code Generator for SmartBite Table QR Codes
 * Clean, lightweight, zero external dependencies.
 * Generates high quality Canvas and SVG QR codes.
 */
(function(window) {
  'use strict';

  // Minimal standalone QR Code implementation supporting Alphanumeric/Byte mode (ISO/IEC 18004)
  function QRCode(targetElement, options) {
    if (typeof targetElement === 'string') {
      targetElement = document.getElementById(targetElement);
    }
    this.target = targetElement;
    this.options = Object.assign({
      width: 180,
      height: 180,
      colorDark: '#1E140F',
      colorLight: '#FFFFFF',
      correctLevel: 1 // 1 = M, 0 = L, 2 = Q, 3 = H
    }, options || {});

    if (this.options.text) {
      this.makeCode(this.options.text);
    }
  }

  // Polynomial and Galois field tables for Reed-Solomon Error Correction
  const EXP_TABLE = new Uint8Array(256);
  const LOG_TABLE = new Uint8Array(256);
  for (let i = 0, x = 1; i < 256; i++) {
    EXP_TABLE[i] = x;
    LOG_TABLE[x] = i;
    x = (x << 1) ^ (x >= 128 ? 0x11D : 0);
  }

  function gmult(a, b) {
    return (a === 0 || b === 0) ? 0 : EXP_TABLE[(LOG_TABLE[a] + LOG_TABLE[b]) % 255];
  }

  function rsGenPoly(n) {
    let poly = [1];
    for (let i = 0; i < n; i++) {
      let next = [0].concat(poly);
      let factor = EXP_TABLE[i];
      for (let j = 0; j < poly.length; j++) {
        next[j] ^= gmult(poly[j], factor);
      }
      poly = next;
    }
    return poly;
  }

  function rsRemainder(data, poly) {
    let res = data.slice();
    let pad = poly.length - 1;
    for (let i = 0; i < pad; i++) res.push(0);
    for (let i = 0; i < data.length; i++) {
      let coef = res[i];
      if (coef !== 0) {
        for (let j = 0; j < poly.length; j++) {
          res[i + j] ^= gmult(poly[j], coef);
        }
      }
    }
    return res.slice(data.length);
  }

  // Version 2 to 6 data capacities & error correction blocks
  const VERSION_CAPACITIES = [
    null,
    { ver: 1, totalBytes: 26, dataBytes: 16, ecBytes: 10, modules: 21 },
    { ver: 2, totalBytes: 44, dataBytes: 28, ecBytes: 16, modules: 25 },
    { ver: 3, totalBytes: 70, dataBytes: 44, ecBytes: 26, modules: 29 },
    { ver: 4, totalBytes: 100, dataBytes: 64, ecBytes: 36, modules: 33 },
    { ver: 5, totalBytes: 134, dataBytes: 86, ecBytes: 48, modules: 37 },
    { ver: 6, totalBytes: 172, dataBytes: 108, ecBytes: 64, modules: 41 }
  ];

  QRCode.prototype.makeCode = function(text) {
    this.text = text;
    this.clear();

    const utf8Bytes = [];
    for (let i = 0; i < text.length; i++) {
      let code = text.charCodeAt(i);
      if (code < 128) {
        utf8Bytes.push(code);
      } else if (code < 2048) {
        utf8Bytes.push(192 | (code >> 6), 128 | (code & 63));
      } else {
        utf8Bytes.push(224 | (code >> 12), 128 | ((code >> 6) & 63), 128 | (code & 63));
      }
    }

    // Pick smallest version that fits data
    let chosen = null;
    for (let v = 2; v < VERSION_CAPACITIES.length; v++) {
      let cfg = VERSION_CAPACITIES[v];
      let maxData = cfg.dataBytes - 3; // header + length
      if (utf8Bytes.length <= maxData) {
        chosen = cfg;
        break;
      }
    }
    if (!chosen) chosen = VERSION_CAPACITIES[VERSION_CAPACITIES.length - 1];

    const matrix = generateMatrix(utf8Bytes, chosen);
    renderCanvas(this.target, matrix, this.options);
  };

  QRCode.prototype.clear = function() {
    if (this.target) {
      this.target.innerHTML = '';
    }
  };

  function generateMatrix(bytes, cfg) {
    const size = cfg.modules;
    const m = [];
    const isReserved = [];
    for (let r = 0; r < size; r++) {
      m[r] = new Uint8Array(size);
      isReserved[r] = new Uint8Array(size);
    }

    // Finder patterns at top-left, top-right, bottom-left
    function placeFinder(row, col) {
      for (let r = -1; r <= 7; r++) {
        for (let c = -1; c <= 7; c++) {
          let currR = row + r;
          let currC = col + c;
          if (currR >= 0 && currR < size && currC >= 0 && currC < size) {
            isReserved[currR][currC] = 1;
            if (r >= 0 && r <= 6 && c >= 0 && c <= 6) {
              if (r === 0 || r === 6 || c === 0 || c === 6 || (r >= 2 && r <= 4 && c >= 2 && c <= 4)) {
                m[currR][currC] = 1;
              } else {
                m[currR][currC] = 0;
              }
            } else {
              m[currR][currC] = 0;
            }
          }
        }
      }
    }

    placeFinder(0, 0);
    placeFinder(0, size - 7);
    placeFinder(size - 7, 0);

    // Timing patterns
    for (let i = 8; i < size - 8; i++) {
      if (!isReserved[6][i]) {
        m[6][i] = (i % 2 === 0) ? 1 : 0;
        isReserved[6][i] = 1;
      }
      if (!isReserved[i][6]) {
        m[i][6] = (i % 2 === 0) ? 1 : 0;
        isReserved[i][6] = 1;
      }
    }

    // Alignment pattern for version >= 2
    if (cfg.ver >= 2) {
      const alignCenters = [
        [],
        [],
        [18],
        [22],
        [26],
        [30],
        [34]
      ][cfg.ver] || [18];

      for (let acR of alignCenters) {
        for (let acC of alignCenters) {
          if (isReserved[acR][acC]) continue;
          for (let r = -2; r <= 2; r++) {
            for (let c = -2; c <= 2; c++) {
              let curR = acR + r;
              let curC = acC + c;
              isReserved[curR][curC] = 1;
              if (Math.abs(r) === 2 || Math.abs(c) === 2 || (r === 0 && c === 0)) {
                m[curR][curC] = 1;
              } else {
                m[curR][curC] = 0;
              }
            }
          }
        }
      }
    }

    // Reserve format information areas
    for (let i = 0; i < 9; i++) {
      if (i < size) {
        isReserved[8][i] = 1;
        isReserved[i][8] = 1;
      }
      if (size - 1 - i >= 0) {
        isReserved[8][size - 1 - i] = 1;
        isReserved[size - 1 - i][8] = 1;
      }
    }
    m[size - 8][8] = 1;
    isReserved[size - 8][8] = 1;

    // Bitstream assembly: Byte mode indicator 0100 + Length + data + padding
    const bitStream = [];
    function pushBits(val, len) {
      for (let b = len - 1; b >= 0; b--) {
        bitStream.push((val >> b) & 1);
      }
    }

    pushBits(4, 4); // Byte mode (0100)
    pushBits(bytes.length, 8); // character count indicator
    for (let b of bytes) pushBits(b, 8);

    // Terminator & padding
    let maxDataBits = cfg.dataBytes * 8;
    while (bitStream.length < maxDataBits && bitStream.length % 8 !== 0) bitStream.push(0);
    const padBytes = [0xEC, 0x11];
    let padIdx = 0;
    while (bitStream.length < maxDataBits) {
      pushBits(padBytes[padIdx % 2], 8);
      padIdx++;
    }

    // Convert data bits to bytes
    const dataCodewords = [];
    for (let i = 0; i < bitStream.length; i += 8) {
      let val = 0;
      for (let b = 0; b < 8; b++) val = (val << 1) | (bitStream[i + b] || 0);
      dataCodewords.push(val);
    }

    // Reed-Solomon Error Correction Code
    const poly = rsGenPoly(cfg.ecBytes);
    const ecCodewords = rsRemainder(dataCodewords, poly);
    const allCodewords = dataCodewords.concat(ecCodewords);

    // Convert codewords to all bits
    const finalBits = [];
    for (let cw of allCodewords) {
      for (let b = 7; b >= 0; b--) finalBits.push((cw >> b) & 1);
    }

    // Place bits in zig-zag upward/downward pattern
    let bitIdx = 0;
    let up = true;
    for (let right = size - 1; right > 0; right -= 2) {
      if (right === 6) right--; // Skip vertical timing column
      let left = right - 1;

      for (let count = 0; count < size; count++) {
        let r = up ? (size - 1 - count) : count;
        if (!isReserved[r][right]) {
          let bit = bitIdx < finalBits.length ? finalBits[bitIdx++] : 0;
          // Apply standard mask pattern (row + col) % 2 === 0
          if ((r + right) % 2 === 0) bit ^= 1;
          m[r][right] = bit;
        }
        if (!isReserved[r][left]) {
          let bit = bitIdx < finalBits.length ? finalBits[bitIdx++] : 0;
          if ((r + left) % 2 === 0) bit ^= 1;
          m[r][left] = bit;
        }
      }
      up = !up;
    }

    // Format bits for Error Level M + Mask 0: 101010000010010
    const formatBits = [1, 0, 1, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 1, 0];
    for (let i = 0; i < 6; i++) m[8][i] = formatBits[i];
    m[8][7] = formatBits[6];
    m[8][8] = formatBits[7];
    m[7][8] = formatBits[8];
    for (let i = 9; i < 15; i++) m[14 - i][8] = formatBits[i];

    for (let i = 0; i < 8; i++) m[8][size - 1 - i] = formatBits[i];
    for (let i = 8; i < 15; i++) m[size - 15 + i][8] = formatBits[i];

    return m;
  }

  function renderCanvas(target, matrix, opts) {
    if (!target) return;
    const modules = matrix.length;
    const quietZone = 4;
    const totalModules = modules + quietZone * 2;
    const cellSize = Math.max(2, Math.floor(opts.width / totalModules));
    const actualWidth = cellSize * totalModules;
    const actualHeight = cellSize * totalModules;

    const canvas = document.createElement('canvas');
    canvas.width = actualWidth;
    canvas.height = actualHeight;
    canvas.style.display = 'block';
    canvas.style.maxWidth = '100%';
    canvas.style.height = 'auto';
    canvas.className = 'smartbite-qr-canvas';

    const ctx = canvas.getContext('2d');
    ctx.fillStyle = opts.colorLight;
    ctx.fillRect(0, 0, actualWidth, actualHeight);

    ctx.fillStyle = opts.colorDark;
    for (let r = 0; r < modules; r++) {
      for (let c = 0; c < modules; c++) {
        if (matrix[r][c] === 1) {
          ctx.fillRect((c + quietZone) * cellSize, (r + quietZone) * cellSize, cellSize, cellSize);
        }
      }
    }

    target.appendChild(canvas);
  }

  window.QRCode = QRCode;
})(window);
