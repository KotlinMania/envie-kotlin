// Inject a safe subset of the build-time process.env into the webpack bundle produced
// by karma-webpack.  This is required for browser targets where `process` is not defined
// at runtime: webpack's DefinePlugin rewrites `process.env[name]` to a static object
// lookup, eliminating the ReferenceError and making standard OS variables (e.g. PATH)
// available during tests.
//
// Only well-known, non-sensitive OS variables are included to avoid leaking CI secrets
// or other credentials that may be present in the full process environment.
const webpack = require('webpack');

const SAFE_ENV_VARS = ['PATH', 'HOME', 'USER', 'SHELL', 'TERM', 'PWD', 'LANG', 'TZ'];
const safeEnv = {};
SAFE_ENV_VARS.forEach(function (key) {
    if (Object.prototype.hasOwnProperty.call(process.env, key)) {
        safeEnv[key] = process.env[key];
    }
});

config.webpack = config.webpack || {};
config.webpack.plugins = (config.webpack.plugins || []).concat([
    new webpack.DefinePlugin({ 'process.env': JSON.stringify(safeEnv) }),
]);
