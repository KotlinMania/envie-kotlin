// Provide a minimal process.env stub for browser test targets.
// Webpack 5 no longer polyfills `process` automatically, so any reference to
// `process.env` in the bundled code would throw a ReferenceError at runtime.
// DefinePlugin replaces `process.env` with a static object literal at bundle
// time, eliminating the reference entirely.
//
// A fixed, non-sensitive stub value for PATH is provided so that
// GetEnvTest.getenv_should_return_a_value_for_path_env_var passes in the
// browser runner without embedding any real process-environment secrets.
const webpack = require('webpack');

config.webpack = config.webpack || {};
config.webpack.plugins = (config.webpack.plugins || []).concat([
    new webpack.DefinePlugin({ 'process.env': JSON.stringify({ PATH: '/usr/bin:/bin' }) }),
]);
