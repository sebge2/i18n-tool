describe('Smoke Tests', function() {

    it('should land on default route', async function() {
        const url = await browser.getCurrentUrl();

        expect(url).toContain('translations');
    });
});